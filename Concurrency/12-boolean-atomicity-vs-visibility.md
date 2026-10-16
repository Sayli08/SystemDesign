
# 12 — Boolean Atomicity vs Visibility in Java

[Home](README.md) | [Related: AtomicInteger vs volatile](11-atomicinteger-vs-int-vs-synchronized.md) | [Java Code](code/BooleanVisibilityDemo.java)

## 1. What Does Atomic Mean?

**Atomicity means an operation happens as one indivisible unit.**

For example:

```java
boolean running = true;
running = false;
```

In Java, reading or assigning a boolean is atomic.

Another thread will see either `true` or `false`, never a partially updated value.

However, this does **not** guarantee that another thread immediately sees the latest value.

## 2. Atomicity vs Visibility vs Thread Safety

| Concept | Meaning | Example |
|---|---|---|
| Atomicity | Operation cannot be partially observed | `running = false` |
| Visibility | Updates made by one thread can be observed by another | `volatile boolean` |
| Thread safety | Overall behavior remains correct with multiple threads | `AtomicBoolean.compareAndSet()` |

**Important:** Atomicity and visibility are separate guarantees.

An operation can be atomic but still have visibility problems.

---

## 3. Normal boolean vs volatile boolean vs AtomicBoolean

| Feature | Normal `boolean` | `volatile boolean` | `AtomicBoolean` |
|---|---|---|---|
| Atomic read | Yes | Yes | Yes |
| Atomic assignment | Yes | Yes | Yes |
| Cross-thread visibility guarantee | No, not by itself | Yes | Yes |
| Simple shared stop flag | Unsafe without coordination | Good choice | Good choice |
| Atomic compare-and-set | No | No | Yes |
| Uses explicit lock | No | No | No |

### Normal boolean

```java
boolean running = true;
running = false;
```

The assignment is atomic, but another thread might not observe the update without synchronization.

### volatile boolean

```java
volatile boolean running = true;
running = false;
```

The assignment is atomic, and volatile provides cross-thread visibility guarantees.

### AtomicBoolean

```java
AtomicBoolean running = new AtomicBoolean(true);

running.set(false);
boolean value = running.get();
```

AtomicBoolean also supports atomic conditional updates.

---

## 4. Why Is volatile Needed for a Shared Flag?

Imagine two threads:

- Main thread changes `running` from `true` to `false`.
- Worker thread continuously checks `running`.

### Without volatile

```java
public class Main {

    private static boolean running = true;

    public static void main(String[] args)
            throws InterruptedException {

        Thread worker = new Thread(() -> {

            while (running) {
                // Keep working
            }

            System.out.println("Worker stopped");
        });

        worker.start();

        Thread.sleep(1000);

        running = false;
    }
}
```

### What could happen?

| Step | Main Thread | Worker Thread |
|---|---|---|
| 1 | `running = true` | Reads true |
| 2 | — | Continues looping |
| 3 | `running = false` | May continue observing true |
| 4 | Expects worker to stop | May never exit |

**Why?**

Without a happens-before relationship, Java does not guarantee that the worker observes the updated value.

The worker might eventually stop, but it is not guaranteed.

---

## 5. Fix Using volatile

```java
public class Main {

    private static volatile boolean running = true;

    public static void main(String[] args)
            throws InterruptedException {

        Thread worker = new Thread(() -> {

            while (running) {
                // Keep working
            }

            System.out.println("Worker stopped");
        });

        worker.start();

        Thread.sleep(1000);

        running = false;

        worker.join();
    }
}
```

### How It Works

```text
Main Thread                 Worker Thread
     |                            |
running = true              Reads true
     |                            |
running = false             Reads false
                                  |
                             Exits loop
```

A volatile write is visible to subsequent volatile reads according to Java's memory model.

**Note:** `volatile` does not guarantee that the worker immediately gets CPU time. It guarantees the memory ordering and visibility needed for this simple stop-flag pattern.

---

## 6. Why Not Just Use a Normal int?

The same visibility problem exists with an integer flag.

```java
private static int running = 1;
```

If one thread changes:

```java
running = 0;
```

another thread is not guaranteed to observe the change without synchronization.

We could use:

```java
private static volatile int running = 1;
```

But a boolean is clearer when the variable represents true or false.

---

## 7. AtomicBoolean

`AtomicBoolean` is a thread-safe boolean class from `java.util.concurrent.atomic`.

```java
import java.util.concurrent.atomic.AtomicBoolean;

AtomicBoolean running = new AtomicBoolean(true);

// Worker:
while (running.get()) {
    // Keep working
}

// Main:
running.set(false);
```

For simple reads and assignments, both volatile boolean and AtomicBoolean can be used.

### Why Do We Need AtomicBoolean?

When multiple threads must conditionally update the same flag.

Suppose only one thread should start a task.

### Unsafe Approach

```java
volatile boolean started = false;

if (!started) {
    started = true;
    startTask();
}
```

Two threads could both read `false` before either writes `true`.

Therefore, both might start the task.

### Safe Approach — compareAndSet()

```java
AtomicBoolean started = new AtomicBoolean(false);

if (started.compareAndSet(false, true)) {
    startTask();
}
```

`compareAndSet(false, true)` performs the check and update atomically.

Only one thread can successfully change the value from false to true.

### Two-Thread Example

| Step | Thread 1 | Thread 2 |
|---|---|---|
| 1 | CAS(false, true) succeeds | — |
| 2 | Value becomes true | — |
| 3 | Starts task | CAS(false, true) fails |
| 4 | — | Does not start task |

**Important:** `compareAndSet()` protects the state transition. If `startTask()` performs other shared operations, those may require additional coordination.

---

## 8. Comparison with AtomicInteger

| Feature | volatile boolean | AtomicBoolean | AtomicInteger |
|---|---|---|---|
| Stores | true/false | true/false | Integer |
| Visibility | Yes | Yes | Yes |
| Atomic read/write | Yes | Yes | Yes |
| compareAndSet() | No | Yes | Yes |
| incrementAndGet() | No | No | Yes |
| Best use | Simple shared flag | Atomic state changes | Shared counter |

### AtomicInteger Example

```java
AtomicInteger counter = new AtomicInteger(0);

counter.incrementAndGet();
```

### AtomicBoolean Example

```java
AtomicBoolean started = new AtomicBoolean(false);

started.compareAndSet(false, true);
```

---

## 9. When Should I Use Each?

| Situation | Choice |
|---|---|
| Variable used by one thread only | Normal boolean |
| Simple shared stop flag | volatile boolean |
| Multiple threads competing to change a flag | AtomicBoolean |
| Multiple threads incrementing a counter | AtomicInteger |
| Multiple related fields must change together | synchronized or ReentrantLock |

---

## 10. Interview Explanation

### What does atomic boolean assignment mean?

"A boolean assignment is atomic because another thread cannot observe a partially written boolean. It sees either true or false."

### Why do we need volatile?

"Even though a boolean assignment is atomic, another thread may not observe the updated value without synchronization. Volatile provides the required visibility."

### Why use AtomicBoolean?

"AtomicBoolean supports atomic conditional updates such as compareAndSet. This is useful when multiple threads compete to change the same flag, and only one should succeed."

---

## 11. Quick Revision

```text
NORMAL BOOLEAN
    → Atomic read/write
    → No cross-thread visibility guarantee by itself

VOLATILE BOOLEAN
    → Atomic read/write
    → Cross-thread visibility
    → Good for simple shared stop flags

ATOMIC BOOLEAN
    → Atomic read/write
    → Cross-thread visibility
    → compareAndSet() for atomic conditional updates

ATOMIC INTEGER
    → Atomic increments/decrements
    → Good for shared counters

SYNCHRONIZED
    → Mutual exclusion
    → Visibility and ordering
    → Protects larger critical sections
```

## Final Takeaway

**Atomicity does not automatically imply visibility, and visibility does not automatically make compound operations atomic.**

- Normal boolean assignment is atomic.
- Volatile boolean provides visibility.
- AtomicBoolean provides visibility and atomic conditional updates.
- AtomicInteger provides atomic integer operations.
- Synchronized protects a critical section using a lock.

---

[Home](README.md)
