# POS Event-Driven System

A Point of Sale (POS) backend architecture built with **Spring Boot** and **Apache Kafka**. This project demonstrates a decoupled system where transaction processing, reporting, and inventory management are handled asynchronously through event streaming.

## System Architecture

The system is divided into two main services:

1. **API Producer Service**:
* Handles incoming sales transactions via REST API.
* Performs initial stock validation.
* Publishes `TransactionEvent` to Kafka topic.
* Manages master data (Products, Categories, Suppliers).


2. **Consumer Service**:
* **Report Consumer**: Asynchronously records transaction history and details to the database.
* **Stock Log Consumer**: Updates product inventory and creates audit trails (Stock Logs) for every movement.


## Key Features

* **Asynchronous Processing**: Sales do not block the main thread; reporting and stock updates happen in the background.
* **Audit Logging**: Every stock change is tracked with types (SALE, RESTOCK, ADJUSTMENT).
* **Stock Integrity**: Built-in protection against negative stock during asynchronous processing.
* **Monorepo Structure**: API and Consumer services managed within a single repository for consistency.

## Tech Stack

* **Backend**: Java 21, Spring Boot 3.x
* **Messaging**: Apache Kafka
* **Database**: PostgreSQL
* **Security**: JWT (JSON Web Token)
* **Documentation**: Scalar

## Getting Started

### Prerequisites

* Docker and Docker Compose
* Java 21 or higher
* Maven

### Installation

1. Clone the repository:
```bash
git clone https://github.com/username/pos-kafka-integrated.git

```


2. Start infrastructure (Kafka & Postgres) using Docker Compose:
```bash
docker-compose up -d

```


3. Run the Consumer Service:
```bash
cd app-backend-consumer
mvn spring-boot:run

```


4. Run the API Producer Service:
```bash
cd app-backend-api
mvn spring-boot:run

```


## API Endpoints

### Transactions

* `POST /api/transactions`: Submit a new sale (Produces Kafka event).

### Inventory

* `POST /api/products/{id}/stock`: Manual stock adjustment (Restock/Set).
* `GET /api/reports/stock-logs`: Retrieve stock movement history.

### Sales Reports

* `GET /api/reports/sales`: Retrieve detailed sales report with date filters.

---
