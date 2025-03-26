package com.jpmc.midascore.serialization;

import com.jpmc.midascore.foundation.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class TransactionSerializer implements Serializer<Transaction> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Transaction data) {
        try {
            return objectMapper.writeValueAsBytes(data); // Serialize the Transaction object to byte array
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // Or throw an exception depending on your error handling strategy
        }
    }

    @Override
    public void close() {
        // Close any resources if needed
    }
}

