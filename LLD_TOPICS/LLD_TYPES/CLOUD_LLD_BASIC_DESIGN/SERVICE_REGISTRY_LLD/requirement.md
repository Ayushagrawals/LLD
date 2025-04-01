Let's start Service Discovery LLD from scratch. Here’s how we will structure it:

📌 Steps to Design Service Discovery LLD
1️⃣ Understanding Requirements (Functional & Non-Functional)
2️⃣ High-Level Architecture (Components & Flow)
3️⃣ Low-Level Class Diagram & APIs
4️⃣ Scaling & Fault Tolerance Considerations
5️⃣ Cloud-Based Implementation (Eureka, AWS Cloud Map, Consul)

I'll start with the requirements and then build out the rest step by step.

1️⃣ Functional & Non-Functional Requirements
✅ Functional Requirements:
Service Registration → A microservice should be able to register itself with the discovery service.

Service Lookup & Discovery → Other services should be able to query and get the address of registered services.

Health Checks → The system should periodically check if registered services are alive.

Load Balancing & Failover → If one instance fails, traffic should be routed to healthy instances.

🛠️ Non-Functional Requirements:
Scalability → Must handle thousands of services dynamically.

Fault Tolerance → Should not be a single point of failure (use leader election, replicas).

Low Latency → Service lookup should be fast and efficient.
