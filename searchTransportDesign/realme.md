# ✈️ Travel Search System using Strategy + Adapter + Facade Patterns

## 🧠 Problem Statement

Design a travel booking/search platform where users can choose between different modes of transport (Flight, Train, Bus). Once a mode is selected, the system should dynamically fetch data from various third-party providers (like Indigo, SpiceJet, etc., in case of flights).

The design should:

- Support future additions of transport modes.
- Support adding new flight/train/bus providers without breaking existing code.
- Keep the integration of multiple providers simple and clean.

---

## 🎯 Design Patterns Used

| Pattern      | Usage in System                                                                                                        |
| ------------ | ---------------------------------------------------------------------------------------------------------------------- |
| **Strategy** | To dynamically choose between transport modes like `FlightTransport`, `TrainTransport`, etc., based on user selection. |
| **Adapter**  | To normalize different third-party provider APIs (e.g., Indigo, SpiceJet) into a common format(We have multiple third party api so each have different sturutre so we are creatingt he adapter pattern here)                     |
| **Facade**   | To act as a single point of access for multiple adapters and encapsulate complexity of using many providers(if we are using adapater  for example indigo adapter or spicejet daoter).           |

---

## 🗂 Class Diagram

![Class Diagram](./class-diagram.png)

_(If you don't have one yet, you can draw using draw.io and export it as `class-diagram.png`)_

---

## 🏗️ System Structure

### Interface

```java
public interface Transport {
    void search(Map<String, String> searchCriteria);
}
```
