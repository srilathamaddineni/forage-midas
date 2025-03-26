package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive = 0.0f;


    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // === Constructors ===

    public TransactionRecord() {
        // Default constructor for JPA
    }

    public TransactionRecord(float amount, UserRecord sender, UserRecord recipient) {
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = LocalDateTime.now();
    }

    // === Getters and Setters ===
    public float getIncentive() {
        return incentive;
    }

    public void setIncentive(float incentive) {
        this.incentive = incentive;
    }

    public Long getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public UserRecord getSender() {
        return sender;
    }

    public void setSender(UserRecord sender) {
        this.sender = sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // === Optional toString for logging/debugging ===

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, amount=%.2f, sender=%s, recipient=%s, timestamp=%s]",
                id, amount, sender.getName(), recipient.getName(), timestamp);
    }
}
