Here's the **updated HLD for Service Discovery** incorporating **cache invalidation & health check sync**:

---

## **🔹 Service Discovery HLD (Including Cache Invalidation & Health Check Sync)**

### **1️⃣ Components**

1. **Clients (Microservices A, B, C, etc.)**

   - Query Service Discovery for the latest service IP & port.
   - Never store or cache service details locally.

2. **Service Discovery System**

   - Maintains a **registry** of active services.
   - Handles **health checks & service failures**.
   - Provides **IP-port resolution** for microservices.
   - Updates DB & Cache when services **register/unregister**.

3. **Database (Source of Truth)**

   - Stores **service mappings** (Service Name → IP, Port, Last Health Check).
   - Used for **permanent records** of available services.

4. **Cache (Redis)**

   - **Speeds up lookups** by storing service mappings temporarily.
   - TTL ensures stale data does not persist.
   - Gets **invalidated on service failures** to prevent outdated data.

5. **Health Checker**

   - Periodically pings registered services.
   - If a service is **unhealthy (fails multiple times)**, it is:
     - Removed from **DB**.
     - **Cache entry is deleted** to prevent stale lookups.

6. **Load Balancer (Optional)**
   - If clients use **multiple replicas**, LB picks the least-loaded instance.
   - Works **alongside** Service Discovery to balance traffic.

---

## **🔹 Service Discovery Flow**

1️⃣ **Service Registration**

- A service (e.g., `order-service`) **registers itself** with Service Discovery.
- Entry added in **DB** (`order-service → IP:Port`).
- **Cache updated** for faster lookups.

2️⃣ **Service Lookup (Client Request Flow)**

- Microservice A needs `order-service`:
  - **Check Redis first** (cache hit ✅ → return IP).
  - If cache **misses**, query the **DB** & update cache.

3️⃣ **Health Check Execution**

- Service Discovery pings every registered service **every 30 sec**.
- If **3 consecutive failures** (90 sec total), service is considered **down**.

4️⃣ **Failure Handling & Cache Invalidation**

- Service **removed from DB** (to prevent stale lookups).
- Redis entry is **deleted** → Future queries go directly to DB.
- Clients doing retries now fetch the **latest service info**.

5️⃣ **Load Balancer (Optional)**

- If multiple replicas exist, clients use **round-robin / least connections** LB strategy.
- Ensures traffic is evenly spread across available instances.

---

## **🔹 Key Enhancements in This Version**

✅ **Health Check Syncs with DB & Cache**  
✅ **Cache TTL & Active Invalidation to Prevent Stale Data**  
✅ **Service Discovery Ensures Fast, Consistent Lookups**  
✅ **Load Balancer Works Seamlessly with Service Discovery**

---

This is now a **fully resilient & scalable** Service Discovery architecture. 🚀  
Would you like me to draw the **final refined HLD** in text or a diagram? 🎨
