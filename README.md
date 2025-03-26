# Midas 
Project repo for the JPMC Advanced Software Engineering Forage program

# Midas Core

**Midas Core** is a Spring Boot-based backend service designed to handle and process financial transactions in real-time using Kafka, persist user and transaction data using an H2 database, and expose RESTful endpoints for querying balances. It forms the heart of a modular financial platform where different components like incentives, analytics, or reporting can be easily integrated and scaled independently. This service listens to Kafka topics for transaction events, validates and records them, adjusts user balances, and integrates with an external Incentive API to award transaction bonuses.

---

## 💡 Features & Tasks Overview

### ✅ Kafka Integration
Midas Core listens to a Kafka topic (configured in `application.yml`) for incoming transactions. It uses a Kafka consumer to deserialize each transaction into a `Transaction` object, which triggers the balance validation and processing workflow. The listener runs continuously and handles transactions in near real-time.

---

### ✅ H2 Database Integration
User data (`UserRecord`) and transaction history (`TransactionRecord`) are stored in an H2 in-memory database using Spring Data JPA. This allows for fast local development and easy switching to a production-ready SQL backend later. The transaction validation ensures that only valid transactions are persisted.

---

### ✅ Transaction Validation & Processing
Each incoming transaction is validated to ensure both sender and recipient exist, and that the sender has sufficient balance. If valid, the transaction is recorded, balances are adjusted, and an incentive is optionally added. Invalid transactions are discarded without modifying the database.

---

### ✅ REST API Integration
Midas Core communicates with a separate Incentive API running on port 8080. After validating a transaction, it sends the transaction as a JSON POST request to `/incentive`, and receives an `Incentive` amount in response. This amount is then added to the recipient’s balance but not deducted from the sender.

---

### ✅ Balance REST API
The service exposes a REST endpoint at `/balance` (port **33400**) that accepts a `userId` as a request parameter. It returns the user's current balance wrapped in a `Balance` object. If the user does not exist, a balance of `0.0` is returned.

