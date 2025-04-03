# Monitoring Thread Dumps & Heap Dumps

## 1. Problem Statement

In a microservices-based system, performance issues such as **high CPU usage, memory leaks, thread starvation, and deadlocks** can occur. While tools like **Spring Boot Actuator** provide real-time metrics (e.g., JVM memory usage, GC stats, active threads), they do **not** capture detailed thread dumps or heap dumps.

### Interview Question:

**"How do you monitor and analyze thread dumps and heap dumps in production? Design a solution for collecting and analyzing these dumps in a distributed system."**

---

## 2. Understanding Thread Dumps & Heap Dumps

### **Thread Dump:**

A snapshot of all active threads in a JVM at a given time. Used to diagnose:

- Deadlocks
- Thread starvation
- High CPU usage due to runaway threads
- Blocked or waiting threads

**Command to Capture:**

```bash
jstack <pid> > thread_dump.txt
```

### **Heap Dump:**

A snapshot of the JVM’s heap memory. Used to diagnose:

- Memory leaks
- Object retention issues
- OutOfMemoryErrors (OOM)

**Command to Capture:**

```bash
jmap -dump:live,format=b,file=heap_dump.hprof <pid>
```

---

## 3. Proposed Monitoring Solution

To automate and integrate **thread/heap dump collection** into a monitoring system, we can design a solution using **Prometheus, Grafana, and a dump collector service**.

### **Architecture Overview**

```
+-------------------------+
|  Application Service    |
|  (Spring Boot, JVM)     |
+-----------+-------------+
            |
            | Actuator (JVM metrics)
            |
+-----------v-------------+
|  Prometheus Agent       |
|  (Scrapes JVM metrics)  |
+-----------+-------------+
            |
            | Alerts on High CPU/Memory
            |
+-----------v-------------+
|  Dump Collector Service |
|  (Triggers jstack/jmap) |
+-----------+-------------+
            |
            | Stores Dumps
            |
+-----------v-------------+
|  Cloud Storage/S3/NFS   |
+-------------------------+
```

### **Steps in the Monitoring Workflow**

1. **Prometheus scrapes JVM metrics** (CPU, memory usage, GC stats, thread count, etc.).
2. **Alert Manager triggers an alert** when CPU/memory crosses a threshold (e.g., CPU > 80%, memory > 90%).
3. **Dump Collector Service triggers thread/heap dumps** when an alert is received.
4. **Dumps are stored in a centralized location** (AWS S3, NFS, or a dedicated dump analysis server).
5. **Developers analyze dumps using tools** (Eclipse MAT, VisualVM, IntelliJ Profiler, etc.).

---

## 4. Implementing the Solution

### **1. Expose a Thread Dump & Heap Dump Endpoint** (Spring Boot)

Enable thread and heap dump endpoints in **Spring Boot Actuator**:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: ["threaddump", "heapdump"]
```

Access dumps via:

- `http://localhost:8080/actuator/threaddump`
- `http://localhost:8080/actuator/heapdump`

### **2. Automate Dump Collection with a Service**

Spring Boot Implementation for Automated Dumps
1️⃣ Add Dependencies
If you're using Micrometer for metrics collection, ensure it's in your pom.xml:

xml
Copy
Edit
<dependency>
<groupId>io.micrometer</groupId>
<artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

This service monitors CPU usage and automatically captures thread/heap dumps when CPU/memory usage crosses a threshold.

java
Copy
Edit
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

@Service
public class DumpMonitorService {

    private static final double CPU_THRESHOLD = 80.0; // 80% CPU usage
    private static final long MEMORY_THRESHOLD = 80 * 1024 * 1024 * 1024L; // 80% of available memory

    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @Scheduled(fixedRate = 10000) // Check every 10 seconds
    public void monitorAndCaptureDumps() {
        double cpuLoad = osBean.getSystemCpuLoad() * 100;
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        if (cpuLoad > CPU_THRESHOLD || usedMemory > MEMORY_THRESHOLD) {
            captureThreadDump();
            captureHeapDump();
        }
    }

    private void captureThreadDump() {
        try {
            Process process = new ProcessBuilder("jstack", String.valueOf(ProcessHandle.current().pid()))
                    .redirectOutput(new File("thread_dump.txt"))
                    .start();
            process.waitFor();
            System.out.println("Thread dump captured.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void captureHeapDump() {
        try {
            Process process = new ProcessBuilder("jmap", "-dump:live,format=b,file=heap_dump.hprof",
                    String.valueOf(ProcessHandle.current().pid()))
                    .start();
            process.waitFor();
            System.out.println("Heap dump captured.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
3️⃣ Enable Scheduling in Spring Boot
Add this to your main class to enable scheduled tasks:

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitoringApplication {
public static void main(String[] args) {
SpringApplication.run(MonitoringApplication.class, args);
}
}
🔹 How This Works
✅ Runs every 10 seconds to check CPU/memory usage.
✅ If usage exceeds the threshold, automatically captures thread & heap dumps.
✅ Dumps are stored as thread_dump.txt and heap_dump.hprof in the application directory.

### **3. Storing & Analyzing Dumps**

- Store dumps in **AWS S3, Google Cloud Storage, or NFS** for later analysis.
- Use **Eclipse MAT, IntelliJ Profiler, or VisualVM** to analyze heap dumps.
- Use **`grep BLOCKED` on thread dumps** to find deadlocks.

---

## 5. Summary

- **Spring Boot Actuator** provides JVM metrics but **does NOT capture dumps**.
- Thread dumps help with **thread contention, deadlocks**.
- Heap dumps help with **memory leaks, OOM issues**.
- **Automate monitoring** using Prometheus & an alert-triggered dump collector.
- **Store & analyze dumps** using tools like Eclipse MAT, IntelliJ Profiler.

### **Final Thought:**

This approach ensures that when an issue occurs, you have **historical dump data** to debug problems **without restarting the service**.
