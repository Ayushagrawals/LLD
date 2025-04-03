# 📘 Monitoring in EKS: Spring Boot Actuator, Kube State Metrics & Thread Dumps

## 📌 Overview

This document explains how **Spring Boot Actuator, Kube State Metrics (KSM), and jstack/jmap** work together to monitor a microservices-based system deployed in **Amazon EKS**. It also clarifies which component collects what information and how metrics are sent to **Prometheus** for visualization in **Grafana**.

---

## 🏗 System Architecture

```plaintext
+-------------------------------+       +---------------------------+       +----------------------+
|    Microservice (Spring Boot) |       | Kubernetes Node (EKS)     |       | Prometheus Server    |
|    - JVM (CPU, memory, GC)    |       | - cAdvisor (CPU, Memory)  |       | - Collects Metrics  |
|    - Actuator (JVM Metrics)   |       | - KSM (Pod-Level Metrics) |       | - Alertmanager       |
+---------------+---------------+       +-----------+---------------+       +---------+------------+
                |                                  |                                 |
                | Actuator exposes JVM Metrics     | KSM exposes container metrics  |
                |                                  |                                 |
                v                                  v                                 v
    +---------------------------+       +--------------------------+       +----------------------+
    | Spring Boot Actuator       |       | Kube State Metrics (KSM) |       | Prometheus Storage  |
    | - CPU Usage (JVM)          |       | - Pod-Level CPU, Memory  |       | - Stores Metrics    |
    | - Memory Usage (JVM Heap)  |       | - Restart Count          |       | - Query via Grafana |
    | - GC Activity              |       | - Resource Requests/Limits |     |                      |
    | - Thread Count             |       | - Pod Restarts, CrashLoop |     |                      |
    +----------------------------+       +--------------------------+       +----------------------+
                |                                  |                                 |
                | jstack captures thread dumps     |                               |
                | if high CPU detected            |                               |
                v                                  v                               v
    +---------------------------+       +--------------------------+       +----------------------+
    | Thread Dump (jstack)       |       | Heap Dump (jmap)        |       | Grafana Dashboard   |
    | - Captures running threads |       | - Captures heap usage   |       | - Visualizes Data   |
    | - Detects Deadlocks        |       | - Detects Memory Leaks  |       |                      |
    +----------------------------+       +--------------------------+       +----------------------+
```

---

## 🔍 What Each Component Collects

| **Component**                | **What It Collects?**                                 | **Sent to Prometheus?** |
| ---------------------------- | ----------------------------------------------------- | ----------------------- |
| **Spring Boot Actuator**     | JVM Metrics (CPU, memory, GC, thread count, requests) | ✅ Yes                  |
| **jstack (Manual Trigger)**  | Thread Dump (Active threads, deadlocks)               | ❌ No (Stored as file)  |
| **jmap (Manual Trigger)**    | Heap Dump (Memory leaks, objects in heap)             | ❌ No (Stored as file)  |
| **Kube State Metrics (KSM)** | Pod/Container Metrics (CPU, memory, restart count)    | ✅ Yes                  |
| **cAdvisor (Node Exporter)** | Node-Level CPU, Memory, Network, Disk Usage           | ✅ Yes                  |
| **Application Logs**         | Logs via Fluentd, Loki, Elasticsearch                 | ✅ Yes (via Loki)       |

---

## 🆚 Actuator vs. KSM: Avoiding Overlap

| **Metric**                  | **Actuator (JVM-Level)**      | **KSM (Pod-Level)** |
| --------------------------- | ----------------------------- | ------------------- |
| **CPU Usage**               | CPU used by the JVM           | CPU used by Pod     |
| **Memory Usage**            | Heap & non-heap memory in JVM | Container memory    |
| **Garbage Collection (GC)** | GC events, frequency          | ❌ Not available    |
| **Thread Count**            | Active JVM threads            | ❌ Not available    |
| **Pod Restart Count**       | ❌ Not available              | Available           |
| **CrashLoopBackOff Events** | ❌ Not available              | Available           |

---

## 🎯 Automated Thread & Heap Dump Collection

### **When Should a Dump Be Captured?**

- **Actuator Metrics (JVM High CPU)** → Capture `jstack`
- **Prometheus Alert (KSM Pod CPU High)** → Capture `jstack`
- **Heap Memory High (JVM Actuator Metric)** → Capture `jmap`

### **Spring Boot Service for Automatic Dumps**

```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

@Service
public class DumpMonitorService {
    private static final double CPU_THRESHOLD = 80.0; // 80% CPU usage
    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @Scheduled(fixedRate = 10000) // Check every 10 seconds
    public void monitorAndCaptureDumps() {
        double cpuLoad = osBean.getSystemCpuLoad() * 100;
        if (cpuLoad > CPU_THRESHOLD) {
            captureThreadDump();
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
}
```

---

## ✅ Final Summary

1️⃣ **Spring Boot Actuator → JVM-Level Metrics** (Thread count, GC, heap memory) → Sent to **Prometheus**.
2️⃣ **Kube State Metrics → Pod-Level Metrics** (CPU, memory, restarts) → Sent to **Prometheus**.
3️⃣ **cAdvisor → Node-Level Metrics** (CPU, memory, disk) → Sent to **Prometheus**.
4️⃣ **Thread & Heap Dumps are NOT sent to Prometheus**, but stored for debugging.
5️⃣ **Grafana visualizes JVM, Pod, and Node-Level Metrics in one place**.

---

## 🎨 Next Steps

Would you like a **Grafana dashboard design** that clearly separates **Actuator (JVM) vs. KSM (Pod) vs. cAdvisor (Node)** metrics? 📊🚀
