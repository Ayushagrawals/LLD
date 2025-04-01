==============================
PRODUCT-FEATURE BASED LLDs
==============================

These are based on features directly used by end-users. They test your ability to design core class structure, interfaces, and business logic.

## Examples:

1. Tic Tac Toe

   - Concepts: 2D board, Player turns, Win check logic
   - Real-world Tag: Game Design

2. Snake & Ladder

   - Concepts: Random dice, Player movement, Board state
   - Real-world Tag: Game Design

3. Parking Lot

   - Concepts: Slot allocation, Vehicle types, Pricing
   - Real-world Tag: Uber/Ola-like backend

4. Splitwise

   - Concepts: Expense tracking, Debt simplification
   - Real-world Tag: Fintech expense sharing

5. Cab Booking System

   - Concepts: Location matching, Booking, Driver state
   - Real-world Tag: Uber, Ola

6. Hotel Booking System

   - Concepts: Room availability, Filters, Calendar logic
   - Real-world Tag: Airbnb, OYO

7. Amazon Cart & Order System

   - Concepts: Cart, Inventory, Checkout
   - Real-world Tag: E-commerce (Amazon)

8. Notification System

   - Concepts: SMS, Email, Push Notification logic
   - Real-world Tag: Any web/mobile app

9. Rate Limiter (Leaky/Token Bucket)
   - Concepts: Throttling, Queueing
   - Real-world Tag: Public APIs

---

## INFRA / TOOL BASED LLDs

These simulate internal tools used by dev teams like SDKs, config systems, log collectors, metrics, etc. They test design patterns, extensibility, and tool-building mindset.

## Examples:

1. Metrics Collector

   - Concepts: CPU/RAM/DB usage tracking, Polling, Observer pattern
   - Real-world Tag: Spring Boot Actuator, Prometheus

2. Search Engine / ElasticSearch

   - Concepts: Inverted Index, Tokenizer, Query parser
   - Real-world Tag: LinkedIn, Netflix

3. Config Management Tool

   - Concepts: Dynamic config fetch, Versioning
   - Real-world Tag: Spring Cloud Config, Firebase Remote Config

4. Feature Flag Service

   - Concepts: Toggle per environment/user, rollout rules
   - Real-world Tag: LaunchDarkly, Unleash

5. API Gateway

   - Concepts: Rate limiting, Authentication, Routing
   - Real-world Tag: Kong, Apigee

6. Log Aggregator

   - Concepts: Log collection, Batching, Storage
   - Real-world Tag: ELK Stack, Datadog

7. Rate Limiter SDK

   - Concepts: Token bucket, Retry logic
   - Real-world Tag: AWS SDK / GCP SDK

8. Distributed Lock Manager

   - Concepts: Locking, Retry, Failover
   - Real-world Tag: Redis, Zookeeper

9. Job Scheduler
   - Concepts: Time-based execution, Retry, Failure recovery
   - Real-world Tag: Airflow-like cron systems

---

## Tips:

- Product LLDs → Focus on OOP, business rules, clear class hierarchy
- Infra LLDs → Focus on design patterns, modularity, extensibility
- Use Interface + Strategy + Observer + Singleton wherever applicable
