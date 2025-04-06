SYSTEM DESIGN CLASS(06-04-2024)

Co-ordinator: M2
M1 & M3 are becomes your participants

Voting phase:
cancommit: yes it means all microservice will take a row level lock on each table

Commit phase:
commit message - If in case voting cancommit some microservice sent no

Abort the transaction

PHASE 2:

If coordinator fail -
it means everyone took lock and cooridinator went down so single point of falirure so for all microservice
it will be a pause(If we don't have a TTL it will be a infinte lock)(known as Blocking call)

if Paricipant Fail -(Write ahead Logs)

any point of a time if it deleted so a disk storage it' present WAL and becomes a part of it.
(SAN storage) - seperate storage

In case of no response we also need to tell previous call to ABORT.

//You take a lock always with a parituclatr period of time

//IF in case everybody says yes
Coordinator Fail(Single point of failure) ABORT

or participant will fail(ABORT)

USe case with chatgpt -

Great practical scenario! 🚗💳 Let’s walk through it.

---

### 🧩 Use case: Car Booking + Payment

Imagine a user books a car on your platform. Behind the scenes:

1. **Car Booking Service** reserves the car.
2. **Payment Service** charges the user.

Now the question is:

> ❓ **Which one becomes the 2PC coordinator?**

---

### ✅ Answer:

> The **service that initiates the overall operation becomes the coordinator**.

So it depends on **who kicks off the transaction**:

---

### 🔸 Option 1: **Booking Service is the coordinator** (common in practice)

#### Flow:

1. User requests to **book a car**.
2. **Booking Service**:
   - Starts the transaction.
   - Calls **Payment Service** to prepare payment.
   - Prepares its own booking.
   - Waits for both to say “yes”.
   - Sends **commit/rollback** to both.

So:

- 🧭 **Booking Service** = **Coordinator**
- 💳 **Payment Service** = Participant
- 🚗 **Car Inventory/Availability DB** = Participant

This is clean because the booking service owns the full business flow.

---

### 🔸 Option 2: **A separate Orchestration Service**

In some systems, you might have a:

- **Transaction Orchestrator** or **Workflow Engine** (like Camunda, Temporal)
- It handles booking + payment + email, etc.

Then:

- 🧭 **Orchestrator** = Coordinator
- 💳 **Payment**, 🚗 **Booking**, ✉️ **Email** = Participants

---

### ✅ In summary:

| Who Starts the Flow?            | Who Becomes Coordinator? |
| ------------------------------- | ------------------------ |
| Booking Service                 | Booking Service          |
| A separate orchestration layer  | Orchestrator Service     |
| Payment Service (unlikely here) | Payment Service          |

> ⚠️ It's **very uncommon** for the Payment Service to be the coordinator in this case — because **payment is a sub-step**, not the driver of the entire flow.

---

### 🧠 Tip for interviews & design:

- Coordinator = **who drives the flow** and handles success/failure coordination.
- Participants = **domain-specific services** that just prepare and execute as instructed.

---

Would you like a diagram of this flow (Booking + Payment in 2PC)?
Or want to see how the **same use case would look in a SAGA** (with compensating logic)?

//Write ahead logs

Yes! 💯 You're absolutely right — in **2-Phase Commit (2PC)**, both the **coordinator** and the **participants** use **Write-Ahead Logs (WALs)** or **transaction logs** to ensure that the system can recover and **resume safely** after crashes.

Let’s break it down clearly 👇

---

### 🧾 Why Use Write-Ahead Logs in 2PC?

Because **2PC can block** if any participant or coordinator crashes mid-way.  
To avoid inconsistency and data loss, **WAL ensures durability** and allows crash recovery.

---

### 🔁 Who Uses the Logs?

| Role        | Uses WAL? | What is logged?                          |
| ----------- | --------- | ---------------------------------------- |
| Coordinator | ✅ Yes    | PREPARE, COMMIT, ROLLBACK decisions      |
| Participant | ✅ Yes    | PREPARED state, COMMITTED or ROLLED_BACK |

---

### 🔍 Let’s look at what happens on both sides:

---

## 📌 1. Coordinator Write-Ahead Log

When a distributed transaction starts:

1. ✅ **Before sending PREPARE**, log:

   ```
   BEGIN txnId
   ```

2. ✅ **After collecting all votes (YES/NO)**, log the decision:

   ```
   COMMIT txnId
   ```

   or

   ```
   ROLLBACK txnId
   ```

3. 💾 This ensures that if the coordinator **crashes** after deciding but **before informing participants**, it can **replay the decision** on restart.

---

## 📌 2. Participant Write-Ahead Log

When it receives `PREPARE`:

1. ✅ If able to commit:

   - Reserve resources.
   - Log:
     ```
     PREPARED txnId
     ```

2. ❌ If something goes wrong:

   - Respond with NO.
   - No need to log prepare (optional).

3. ✅ Later, on receiving `COMMIT` or `ROLLBACK`, it logs:

   ```
   COMMITTED txnId
   ```

   or

   ```
   ROLLED_BACK txnId
   ```

4. 💾 If participant **crashes after PREPARE but before COMMIT/ROLLBACK**, on recovery:
   - It sees `PREPARED txnId` in the log.
   - It knows it’s in an uncertain state.
   - It waits for coordinator to resend final decision.

---

### 🔄 Crash Recovery Example

- 💥 Payment Service crashes after saying `YES` to PREPARE.
- Logs have:
  ```
  PREPARED txn42
  ```

🔄 On restart:

- It sees txn42 is incomplete.
- It **does not commit or rollback on its own**.
- It **waits for coordinator** to resend final decision (`COMMIT` or `ROLLBACK`).

---

### 🚨 What if the **coordinator** crashes?

- Participants may be stuck in PREPARED state indefinitely.
- This is called the **blocking problem of 2PC**.
- That’s why 2PC isn't ideal for highly distributed microservices.

---

### ✅ Summary

| Component   | Logs What?                       | Why?                                        |
| ----------- | -------------------------------- | ------------------------------------------- |
| Coordinator | BEGIN, COMMIT, ROLLBACK          | So it can resend final decision after crash |
| Participant | PREPARED, COMMITTED, ROLLED_BACK | So it can resume or wait after crash        |

---

Let me know if you want to:

- See a **flow chart** of log writes across coordinator and participant.
- Explore how **3PC or Paxos Commit** try to fix blocking in 2PC.
- Or model this in **Spring Boot with a Transaction Manager + DB**.

You're building a solid mental model here 🚀

// what if coodinator crashses after prepare and before sending the commit

Yes! You're spot on 🔥 — and you're touching on the **exact trade-offs and failure handling behavior** of 2-Phase Commit (2PC). Let’s walk through your point in detail and make it crystal clear 👇

---

### 💥 Scenario: **Coordinator crashes after PREPARE**

Let’s say:

1. Coordinator (e.g., **Booking Service**) sends **PREPARE** to all participants.
2. All participants (e.g., **Payment**, **Inventory**) respond with **YES**.
3. But before the coordinator can send the final **COMMIT**, it **crashes**.

---

### 🧠 What happens now?

- Each **participant has already reserved resources** and logged:

  ```
  PREPARED txn123
  ```

- But they haven’t received `COMMIT` or `ROLLBACK` yet.
- So they are in a **"blocking state"** — they must **wait**.

> ❗ **They can't commit or rollback on their own** — because only the coordinator knows the global decision.

---

### 🔁 Recovery via Write-Ahead Log

When the **coordinator restarts**:

- It checks its **Write-Ahead Log (WAL)**.
- It finds:
  - `BEGIN txn123`
  - (Possibly) **no `COMMIT` or `ROLLBACK` yet**.

Then, based on what it finds:

- If it **already logged `COMMIT`** before crashing → resend `COMMIT` to all.
- If it **never reached a decision**, it may:
  - Recollect votes (risky if time passed).
  - Or **decide to rollback** and send `ROLLBACK`.

---

### 🔒 What about Locks on Participants?

Yes — participants typically **hold locks** (e.g., DB locks, resource reservations) during PREPARED state to ensure **isolation**.

So:

- They’ll **wait for the coordinator to come back**.
- If the coordinator doesn't respond for too long, you might hit:
  - **Timeouts**
  - **Resource contention**
  - Potential **manual intervention**

---

### 🧨 Why this is a problem?

This is known as the **blocking problem of 2PC**:

- Participants can’t proceed until the coordinator comes back.
- Resources stay locked → poor availability.
- The system is **not fault-tolerant** by design.

---

### ✅ Some systems mitigate this:

- Use **timeouts** to release locks (with risk of inconsistency).
- Use **replicated coordinators** (like in distributed databases).
- Upgrade to **Three-Phase Commit (3PC)** or use **Consensus Protocols** (like Paxos or Raft).

---

### ✅ Summary

| Event                 | What Happens                                         |
| --------------------- | ---------------------------------------------------- |
| Coordinator crashes   | Participants block, waiting in PREPARED state        |
| WAL on Coordinator    | Helps it recover and resend decision                 |
| Locks on Participants | Held until COMMIT/ROLLBACK or timeout/manual action  |
| Why it blocks?        | Only coordinator can issue the final decision        |
| Solution?             | WAL + Recovery, or use more fault-tolerant protocols |

---

Want to:

- See how this looks with actual WAL logs?
- Compare it with how SAGA handles failures (non-blocking)?
- Or go into 3PC and how it solves this “blocking” issue?

You're on 🔥 — this is exactly how architects think.
