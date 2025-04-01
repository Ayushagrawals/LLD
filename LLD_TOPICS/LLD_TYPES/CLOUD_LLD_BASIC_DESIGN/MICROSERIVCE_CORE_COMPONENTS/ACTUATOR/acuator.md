Q - WHAT ACTUATOR CONTAINS ?

ANs - The **`/actuator/prometheus`** endpoint in a Spring Boot application provides **metrics** related to the application that can be scraped by Prometheus. These metrics are used for monitoring the health and performance of the application and are typically exposed in a format that Prometheus can consume.

Here are the key types of information that the **`/actuator/prometheus`** endpoint provides:

### 1. **JVM Metrics:**

These metrics provide insights into the health of the Java Virtual Machine (JVM) running your application.

- **Memory Usage**: Shows the amount of memory used by the JVM, including heap and non-heap memory.
- **Garbage Collection Metrics**: Provides metrics related to garbage collection, such as the number of collections, time spent in garbage collection, and the memory reclaimed.
- **Thread Metrics**: Information about the number of active threads, such as the number of live threads, daemon threads, and peak thread counts.

Example:

```
jvm_memory_bytes_used{area="heap"} 10485760.0
jvm_memory_bytes_used{area="non-heap"} 2048000.0
jvm_gc_seconds_count{gc="young"} 20
```

### 2. **Application Metrics:**

Metrics related to the Spring Boot application itself, such as:

- **Request Metrics**: Number of requests processed by the application, response times, error rates, etc.
- **Active Sessions**: Number of active HTTP sessions if you're using session-based authentication.
- **Health Metrics**: Includes the status of your application's health checks, like `UP` or `DOWN`.

Example:

```
http_server_requests_seconds_count{status="200",method="GET",uri="/api/products"} 150
http_server_requests_seconds_sum{status="200",method="GET",uri="/api/products"} 10.3
```

### 3. **Spring Boot Actuator Metrics:**

These are custom metrics provided by Spring Boot Actuator endpoints.

- **Endpoint Metrics**: This includes metrics for all exposed endpoints (e.g., `/health`, `/info`) showing the number of calls, response times, etc.
- **Database Metrics**: For example, the number of database queries and the time spent on database calls if you're using Spring Data.

Example:

```
spring_boot_version{version="2.5.0"} 1
spring_cache_size{cache="users"} 1024
```

### 4. **Custom Application Metrics:**

If you've created any custom metrics in your application, such as counters, gauges, histograms, or timers, these will also be exposed at the `/actuator/prometheus` endpoint.

Example of custom counter metric:

```
custom_requests_received_total{endpoint="/api/orders"} 100
```

### 5. **System Metrics:**

These metrics provide insights into the underlying system resources.

- **CPU Usage**: Metrics related to the CPU usage of the application, such as the percentage of CPU used.
- **File Descriptors**: Information about the number of file descriptors used by the application.

Example:

```
system_cpu_usage{cpu="cpu0"} 0.95
system_filesystem_free_bytes{mount="root"} 204800000
```

### 6. **Prometheus-Specific Metrics:**

Prometheus exposes its own set of internal metrics for scraping, including:

- **Scrape duration**: How long it takes Prometheus to scrape the data.
- **Target availability**: Whether the Prometheus target (your application) is available or not.

Example:

```
prometheus_target_last_scrape_duration_seconds 0.123
prometheus_target_last_scrape_timestamp_seconds 1623878400
```

### 7. **Other Spring Boot Actuator Metrics (Optional):**

Depending on the Spring Boot Actuator setup and configuration, you can expose various additional metrics such as:

- **Cache Metrics**: Information about the caches used in the application (e.g., cache hits, misses, and eviction rates).
- **Health Metrics**: Status of various services integrated into your application (e.g., database health, disk space usage).

Example:

```
cache_puts{cache="userCache"} 300
health_check{component="db"} 1
```

### Key Notes:

- The **Prometheus** endpoint in Spring Boot actuator provides metrics in the **Prometheus format** (plaintext), which Prometheus can scrape directly.
- By default, the `/actuator/prometheus` endpoint exposes metrics in a machine-readable format suitable for Prometheus. You can enable/disable specific metrics by configuring the Spring Boot application properties (e.g., in `application.yml` or `application.properties`).
- You can customize which metrics are exposed via Spring Boot's configuration files and enable specific modules, such as `metrics`, `health`, `logging`, and more.

### Configuration Example:

In your `application.yml`, you can enable the Prometheus endpoint and configure which metrics to expose:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    enable:
      jvm: true
      http.server.requests: true
      system: true
```

This configuration will expose the **JVM**, **HTTP server requests**, and **system** metrics through the `/actuator/prometheus` endpoint.

////////////////////////////////////////////////////////////////////////////////
Q - so lets come to acuator lld what is the question is above discussion

The **Actuator LLD** (Low-Level Design) question would likely focus on how you would **expose, monitor, and manage health, metrics, and other operational aspects** of your Spring Boot application using **Spring Boot Actuator** and its integration with tools like **Prometheus** for monitoring.

Given our discussion about **Prometheus integration**, the main focus would be on:

### Key Aspects to Cover in an Actuator LLD:

1. **Expose Metrics with Spring Boot Actuator**:

   - Design how to expose application-level metrics (such as JVM metrics, HTTP request metrics, custom metrics) through the `/actuator/prometheus` endpoint for Prometheus to scrape.
   - What are the key metrics that should be exposed? For example, JVM memory usage, garbage collection times, thread counts, HTTP request/response details, custom business metrics, etc.
   - What configuration options are required to control which metrics to expose and how to customize them?

2. **Health Checks and Endpoints**:

   - Define health check behavior using `/actuator/health` and `/actuator/info`. What custom health checks would you implement (e.g., database health, custom service checks)?
   - Design a failure response mechanism. For example, when a service is down (e.g., database or an external API), how does the health check respond? What does the Prometheus monitoring system report about unhealthy instances?

3. **Prometheus Integration**:

   - How will Prometheus scrape metrics from your Spring Boot application?
   - What is the `/actuator/prometheus` endpoint, and how does it present the data in a format that Prometheus understands?
   - How to expose the right set of metrics for Prometheus to gather and alert on?
   - How to secure and configure the endpoint to expose the data securely (e.g., authentication, rate limiting, etc.)?

4. **Custom Metrics**:

   - How would you add custom application metrics, like tracking the number of orders processed, average response time, etc., and expose them via Actuator to be scraped by Prometheus?
   - Would you create custom counters, gauges, or histograms for business-specific metrics?

5. **Security & Access Control**:

   - How would you manage access to the actuator endpoints? Would you secure the `/actuator/health`, `/actuator/prometheus`, and other sensitive endpoints with authentication and authorization?
   - How do you control which endpoints are exposed for monitoring in production environments?

6. **Alerts & Monitoring**:

   - How would Prometheus alert you when a health check fails, or certain metrics exceed thresholds (e.g., high memory usage, slow response times)?
   - What kind of actions or alerts would be triggered from Prometheus when a threshold for a custom metric is reached?

7. **Handling Multiple Environments**:
   - How would you design the system to handle different configurations for multiple environments (e.g., dev, staging, prod) while keeping the same monitoring setup? Would different sets of metrics be exposed depending on the environment?

### Example of an Interview Question on Actuator LLD:

1. **Question**: _"Design a monitoring solution using Spring Boot Actuator for a microservice application. The application should expose key metrics, health checks, and custom application metrics. Integrate with Prometheus for scraping, and ensure that the system is highly configurable and secure. How would you set up actuator endpoints, customize the metrics, and integrate with Prometheus for monitoring and alerting?"_

### Answer Components:

1. **Endpoints and Metrics**:

   - Expose `/actuator/prometheus` for Prometheus to scrape.
   - Expose `/actuator/health` for health checks, with custom checks for database and external service health.
   - Configure Spring Boot Actuator to expose JVM metrics, HTTP server metrics, and application-specific metrics.

2. **Custom Metrics**:

   - Use Spring Boot's `MeterRegistry` to create custom metrics like counters or timers for business logic (e.g., tracking the number of orders processed, average processing time).
   - Configure Spring Boot Actuator to expose these custom metrics through the `/actuator/prometheus` endpoint.

3. **Prometheus Configuration**:

   - Prometheus scrapes the `/actuator/prometheus` endpoint regularly (every 15-30 seconds).
   - Set up Prometheus to scrape the Spring Boot application, and create alerts based on critical metrics, like high response times or unhealthy services.

4. **Security**:

   - Secure the actuator endpoints (e.g., `/actuator/health`, `/actuator/prometheus`) using Spring Security, restricting access to authorized users or services.
   - Enable basic authentication or OAuth for securing sensitive endpoints.

5. **Alerting**:
   - Set up Prometheus alerting rules to trigger when certain metrics exceed thresholds (e.g., if `http_server_requests_seconds_count` exceeds a certain value or if the JVM heap memory usage exceeds a threshold).
   - Use Alertmanager to route alerts to appropriate teams or systems (e.g., Slack, email).

### What You Need to Design in the LLD:

- **Service Architecture Diagram** showing the integration of Spring Boot Actuator, Prometheus, and the microservice architecture.
- **Detailed Configuration** for Spring Boot Actuator endpoints (`application.yml` or `application.properties`), including which metrics and health checks are enabled.
- **Flow** of how Prometheus scrapes the `/actuator/prometheus` endpoint, stores the metrics, and triggers alerts based on thresholds.
- **Security Mechanism** for securing actuator endpoints, if necessary.
- **Custom Metrics** for business-specific logic.

### Diagram for LLD:

- **Spring Boot Microservice**: Exposes `/actuator/prometheus`, `/actuator/health`, `/actuator/info`.
- **Prometheus**: Scrapes the `/actuator/prometheus` endpoint at regular intervals.
- **Alertmanager**: Receives alerts from Prometheus and triggers notifications to external systems (e.g., Slack, Email).

This kind of design helps ensure that your application is continuously monitored, and any issues (such as high response times or failed health checks) are identified promptly.
