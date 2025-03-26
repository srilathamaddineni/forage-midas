package com.jpmc.midascore.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class KafkaTransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(String message) {
        try {
            // Deserialize incoming transaction
            Transaction tx = objectMapper.readValue(message, Transaction.class);

            // Validate sender and recipient exist
            Optional<UserRecord> senderOpt = Optional.ofNullable(userRepository.findById(tx.getSenderId()));
            Optional<UserRecord> recipientOpt = Optional.ofNullable(userRepository.findById(tx.getRecipientId()));

            if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            // Validate balance
            if (sender.getBalance() < tx.getAmount()) return;

            // Call Incentive API
            Incentive incentive = restTemplate.postForObject(
                    "http://localhost:8080/incentive",
                    tx,
                    Incentive.class
            );

            float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

            // Update balances
            sender.setBalance(sender.getBalance() - tx.getAmount());
            recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);

            userRepository.save(sender);
            userRepository.save(recipient);

            // Record transaction with incentive
            TransactionRecord record = new TransactionRecord();
            record.setAmount(tx.getAmount());
            record.setSender(sender);
            record.setRecipient(recipient);
            record.setIncentive(incentiveAmount);
            record.setTimestamp(LocalDateTime.now());

            transactionRepository.save(record);

            // Optional: Print user balances for debugging
            userRepository.findAll().forEach(user ->
                    System.out.println(user.getName() + " → Balance: " + user.getBalance() + ", ID: " + user.getId())
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}