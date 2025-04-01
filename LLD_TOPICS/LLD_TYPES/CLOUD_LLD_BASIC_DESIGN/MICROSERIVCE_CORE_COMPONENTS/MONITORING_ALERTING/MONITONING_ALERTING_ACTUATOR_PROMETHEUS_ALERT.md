# Monitoring System LLD Using Spring Boot Actuator, Prometheus, and Grafana

## **1. Problem Statement**

Design a **Monitoring System for Microservices** that collects metrics, stores them in a time-series database, visualizes them, and alerts when thresholds are breached.

---

## **2. High-Level Components**

- **Spring Boot Microservice** → Exposes metrics using Actuator.
- **Prometheus** → Collects metrics from the microservice.
- **AlertManager** → Sends alerts based on metric thresholds.
- **Grafana** → Visualizes the collected metrics.
- **Node Exporter / Kube-State-Metrics** → Collects system-level Kubernetes metrics.

---

## **3. LLD Design - Step-by-Step**

### **1️⃣ Expose Metrics in Spring Boot**

#### **Add Dependencies**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

#### **Enable Actuator and Prometheus Endpoint**

```properties
management.endpoints.web.exposure.include=health,metrics,info,prometheus
management.endpoint.health.show-details=always
```

#### **Custom Metric Example**

```java
@RestController
public class OrderController {
    private final MeterRegistry meterRegistry;

    public OrderController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/order")
    public String placeOrder() {
        meterRegistry.counter("orders_placed_total").increment();
        return "Order placed!";
    }
}
```

🔹 **Metrics available at:** `/actuator/prometheus`

---

### **2️⃣ Configure Prometheus**

#### **Prometheus Configuration (`prometheus.yml`)**

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "spring-boot-app"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["spring-boot-app-service:8080"]

  - job_name: "kubernetes-nodes"
    static_configs:
      - targets: ["node-exporter:9100"]
```

🔹 **Prometheus scrapes:**

- Microservice metrics from `/actuator/prometheus`
- System metrics from Node Exporter

---

### **3️⃣ Configure Grafana**

- Add **Prometheus as a Data Source**.
- Create **Dashboards** with queries:

🔹 **Total HTTP Requests**

```promql
http_server_requests_seconds_count
```

🔹 **Average Response Time**

```promql
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
```

🔹 **CPU Usage**

```promql
rate(container_cpu_usage_seconds_total[1m])
```

---

### **4️⃣ Set Up AlertManager**

#### **Prometheus Alert Configuration**

```yaml
rule_files:
  - "alert-rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets: ["alertmanager:9093"]
```

#### **Alert Rule Example (`alert-rules.yml`)**

```yaml
groups:
  - name: High CPU Usage
    rules:
      - alert: HighCPUUsage
        expr: rate(container_cpu_usage_seconds_total[5m]) > 0.7
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "High CPU usage detected"
          description: "CPU usage is above 70% for more than 1 minute"
```

🔹 **Triggers an alert if CPU usage exceeds 70% for 1 minute.**

---

## **4. LLD Diagram**

```
+----------------+          +----------------+         +--------------+       +-----------+
|  Spring Boot  |  --->    |  Prometheus    |  --->   |  Grafana     |  ---> |  Alerts   |
| (Actuator)    |          | (Scrapes Data) |         | (Visualize)  |       | (Slack/Email)
+----------------+          +----------------+         +--------------+       +-----------+
```

---

## **5. How Actuator Metrics Appear in Prometheus**

When **Prometheus scrapes `/actuator/prometheus`**, it gets data like:

```text
http_server_requests_seconds_count{method="GET", status="200", uri="/orders"} 100.0
http_server_requests_seconds_sum{method="GET", status="200", uri="/orders"} 50.0
process_cpu_usage 0.012
system_cpu_usage 0.24
jvm_memory_used_bytes{area="heap"} 12345678
jvm_memory_max_bytes{area="heap"} 987654321
```

🔹 **Key Metrics**:

- `http_server_requests_seconds_count` → Total HTTP requests.
- `http_server_requests_seconds_sum` → Total response time in seconds.
- `process_cpu_usage` → CPU used by the app.
- `system_cpu_usage` → Overall system CPU usage.
- `jvm_memory_used_bytes` → Heap memory in use.

---

## **6. Summary**

✔️ **LLD for monitoring system** covering Actuator, Prometheus, AlertManager, and Grafana.  
✔️ **How Prometheus scrapes data & how the schema looks.**  
✔️ **How to query Prometheus metrics & visualize in Grafana.**  
✔️ **How to set up alerts for high CPU, errors, etc.**

---

## **7. Next Steps**

1️⃣ Implement this setup on a local or cloud-based Kubernetes cluster.  
2️⃣ Extend it by adding **distributed tracing with OpenTelemetry or Jaeger**.  
3️⃣ Practice **PromQL queries** for advanced filtering.

🚀 **Happy Monitoring!**
