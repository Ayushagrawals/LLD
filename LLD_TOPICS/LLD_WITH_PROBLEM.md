| Day(s) | Problem             | Type    | Key Patterns                  | Reused In                               |
| ------ | ------------------- | ------- | ----------------------------- | --------------------------------------- |
| 1–2    | Tic Tac Toe         | Product | OOP, State                    | Cab Booking, Splitwise                  |
| 3–4    | Snake and Ladder    | Product | Factory, Game Loop            | Scheduler, Booking systems              |
| 5–6    | Parking Lot         | Product | Strategy, Enum, Interface     | Cab Booking, Hotel Booking              |
| 7–8    | Metrics Collector   | Infra   | Strategy, Observer, Singleton | Log Aggregator, Prometheus-style system |
| 9–10   | Notification System | Product | Strategy, Observer            | Splitwise, Orders, Feature Flag systems |

| Day(s) | Problem              | Type    | Key Patterns                  | Reused In                         |
| ------ | -------------------- | ------- | ----------------------------- | --------------------------------- |
| 11–12  | Splitwise            | Product | Observer, Strategy            | Wallets, Group Tracking           |
| 13–14  | Cab Booking System   | Product | State, Strategy               | Hotel Booking, Order Workflows    |
| 15–16  | Feature Flag Service | Infra   | Singleton, Observer, Strategy | Config System, API Gateway        |
| 17–18  | Rate Limiter SDK     | Infra   | Token Bucket, Thread Safety   | API Gateway, Locking tools        |
| 19–20  | Job Scheduler (cron) | Infra   | Timer, Priority, Observer     | Distributed Locks, Aggregators    |
| 21–22  | API Gateway          | Infra   | Strategy, Decorator, Adapter  | E-Commerce, Notification Dispatch |

| Day(s) | Problem                        | Type    | Key Patterns                  | Reused In                    |
| ------ | ------------------------------ | ------- | ----------------------------- | ---------------------------- |
| 23–24  | Hotel Booking System           | Product | Factory, Strategy, Calendar   | Cab, Order Mgmt              |
| 25–26  | ElasticSearch (Basic Index)    | Infra   | Builder, Adapter, Tokenizer   | Log Aggregator, Search Tools |
| 27–28  | Config Management System       | Infra   | Observer, Singleton, Strategy | Feature Flags, Versioning    |
| 29–30  | Distributed Lock Manager       | Infra   | Singleton, Locking            | Rate Limiter, Scheduler      |
| 31–32  | E-Commerce Order System        | Product | Strategy, Builder, State      | Combines all modules         |
| 33–34  | Log Aggregator (Fluentd style) | Infra   | Observer, Decorator, Strategy | Metrics Collector, Log Infra |

| Pattern   | First Learned In          | Reused In                                    |
| --------- | ------------------------- | -------------------------------------------- |
| Strategy  | Parking Lot, Notification | Booking, Metrics, API Gateway, Order Systems |
| Observer  | Notification              | Metrics, Log Aggregator, Config Systems      |
| Singleton | Feature Flag              | Config, Lock Manager, Cache Wrappers         |
| State     | Tic Tac Toe, Cab Booking  | Hotel Booking, Orders, Game Engines          |
| Factory   | Snake & Ladder            | Booking, Order System                        |
| Decorator | API Gateway               | Logging, Tracing, Security Middleware        |
