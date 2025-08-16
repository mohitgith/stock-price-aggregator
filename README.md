# stock-price-aggregator

## Services Overview

We’ll have five services + support components

| Service Name| Purpose |
| -------- | -------- |
| Stock-Fetcher Service   | Fetches stock prices from multiple external APIs concurrently. Handles scheduling and parallel calls.  |
| Stock-Aggregator Service  | Combines data from fetchers, calculates averages, detects anomalies, and stores results.   |
| Notification Service  |  Sends alerts when price thresholds are crossed (email, SMS, push).  |
| Stock-Query Service  | Exposes a GraphQL API for querying historical & real-time stock data.   |
| User-Preference Service  | Stores user alert preferences (e.g., threshold values, interested stocks).   |

## Detailed Service Breakdown

### 1. Stock-Fetcher Service

### Purpose
* Periodically poll multiple stock market APIs (e.g., Yahoo Finance, Alpha Vantage, mock APIs for demo).
* Use multithreading (Java 8 `CompletableFuture`, Java 17 enhanced switch, parallel streams) to fetch data in parallel.
* Push data to a message broker (e.g., Kafka) or directly to Stock-Aggregator Service.

### Concurrency Concepts:
* Thread pools for multiple API calls.
* `CompletableFuture.allOf()` to aggregate results.
* ScheduledExecutorService for polling every N seconds.

### Dependencies:
* `spring-boot-starter-web` (REST API calls if needed)
* `spring-boot-starter` (base)
* `spring-boot-starter-json` (parse API responses)
* `org.springframework:spring-context` (scheduling)
* `org.apache.httpcomponents:httpclient` or `java.net.http` (Java 11+) for HTTP requests
* (Optional) `spring-kafka` if using Kafka for async data transfer

### 2. Stock-Aggregator Service

### Purpose
* Receive raw stock data from Stock-Fetcher Service.
* Merge multiple sources for the same stock (resolve discrepancies).
* Detect anomalies or trends (big changes, sudden spikes).
* Store aggregated results in DB (Liquibase-managed).

### Concurrency Concepts:
* ConcurrentHashMap for aggregation state.
* Locks for synchronized updates.
* ForkJoinPool for processing large batches in parallel.

### Dependencies:
* `spring-boot-starter-data-jpa` (DB access)
* `spring-boot-starter` (base)
* `org.postgresql:postgresql` (PostgreSQL driver)
* `org.liquibase:liquibase-core` (schema management)
* (Optional) `spring-kafka` (if consuming Kafka messages)

### 3. Notification Service

### Purpose:
* Subscribe to events from Stock-Aggregator Service (price threshold crossed).
* Send notifications via email, SMS, or push (mock for demo).
* Uses thread pools to send notifications in parallel.

### Concurrency Concepts:
* Producer-consumer model for notifications.
* CompletableFuture for async sending.

### Dependencies:
* `spring-boot-starter` (base)
* `spring-boot-starter-mail` (email sending)
* `spring-kafka` (if using event-driven architecture)
* `spring-boot-starter-json` (for message parsing)

### 4. Stock-Query Service (GraphQL API)

### Purpose:
* Provide a GraphQL API to query:
* Current stock price
* Historical trends
* User-specific alerts
* Allow querying multiple stocks in a single request.

### Concurrency Concepts:
* Parallel fetching of historical and current data.

### Dependencies:
* `spring-boot-starter-graphql` (GraphQL support)
* `spring-boot-starter-data-jpa` (DB access)
* `org.postgresql:postgresql` (PostgreSQL driver)
* `org.liquibase:liquibase-core` (schema management)

### 5. User-Preference Service

### Purpose:
* Store user settings: which stocks to track, alert thresholds, notification channels.
* Expose CRUD API for preferences (GraphQL or REST).
* Integrates with Notification Service for alerting rules.

### Concurrency Concepts:
* Atomic updates for user settings.
* ConcurrentHashMap cache for quick access.

### Dependencies:
* `spring-boot-starter-web` (REST) or spring-boot-starter-graphql (GraphQL)
* `spring-boot-starter-data-jpa` (DB access)
* `org.postgresql:postgresql` (PostgreSQL driver)
* `org.liquibase:liquibase-core`

## Supporting Components
   |Component | Purpose | 	Dependencies                            |
   | -------- | -------- |------------------------------------------| 
   |PostgreSQL Database | Store all aggregated stock data, preferences, and notifications. | `org.postgresql:postgresql`, Liquibase     |
   |Kafka / RabbitMQ (optional)	| Event-driven communication between services. | `spring-kafka` or `spring-boot-starter-amqp` |
   |Liquibase |DB schema versioning and migrations. | `org.liquibase:liquibase-core`             |
