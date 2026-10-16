
# 11 — int vs volatile vs AtomicInteger vs synchronized

[Home](README.md) | [Related: Thread Safety](02-thread-safety.md) | [Counter Code](code/CountersDemo.java) | [Volatile vs Atomic Code](code/VolatileVsAtomicDemo.java)

## 1. What Is AtomicInteger?

`AtomicInteger` is a thread-safe integer class provided by `java.util.concurrent.atomic`.

It allows multiple threads to safely update a shared counter without losing increments.

**Key distinction:**

- `int` — Normal integer variable.
- `volatile int` — Provides visibility across threads, but not atomic increments.
- `AtomicInteger` — Provides visibility and atomic operations.
- `synchronized` — Protects shared operations using a lock.

---

## 2. Normal int vs volatile vs AtomicInteger vs synchronized

| Feature | Normal `int` | `volatile int` | `AtomicInteger` | `synchronized int` |
|---|---|---|---|---|
| Atomic assignment | Yes | Yes | Yes | Yes |
| Cross-thread visibility guaranteed | No | Yes | Yes | Yes, with consistent locking |
| Thread-safe `count++` | No | No | Yes, using `incrementAndGet()` | Yes, inside lock |
| Uses locking | No | No | No explicit lock | Yes |
| Best use | Local counter | Shared status/flag | Shared counter | Multiple related operations |

**Important:** `synchronized int` is not a Java type. It means a normal `int` accessed through properly synchronized methods or blocks.

A normal `int` can also be safely shared when all accesses are protected by appropriate synchronization.

---

## 3. Normal int — Race Condition

```java
int count = 0;

count++; // Not atomic
```

`count++` involves three logical steps:

1. Read current value.
2. Add 1.
3. Write updated value.

### Two-thread example

| Step | Thread 1 | Thread 2 |
|---|---|---|
| 1 | Reads 0 | |
| 2 | | Reads 0 |
| 3 | Calculates 1 | Calculates 1 |
| 4 | Writes 1 | Writes 1 |

Expected result: `2`

Possible actual result: `1`

This is called a **race condition**, because the result depends on how the operations of different threads overlap.

---

## 4. AtomicInteger — Thread-Safe Counter

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger count = new AtomicInteger(0);

count.incrementAndGet();
count.incrementAndGet();

System.out.println(count.get()); // 2
```

Each `incrementAndGet()` executes atomically.

### Two-thread example

```java
AtomicInteger count = new AtomicInteger(0);

Runnable task = () -> {
    for (int i = 0; i < 1000; i++) {
        count.incrementAndGet();
    }
};

Thread t1 = new Thread(task);
Thread t2 = new Thread(task);

t1.start();
t2.start();

t1.join();
t2.join();

System.out.println(count.get()); // 2000
```

Both threads safely increment the same counter.

### Why does it work?

`AtomicInteger.incrementAndGet()` performs the update atomically, so concurrent increments do not overwrite each other.

It typically relies on hardware-supported atomic operations such as compare-and-swap.

---

## 5. synchronized — Alternative Solution

```java
class Counter {

    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

The synchronized methods protect `count` using the object's intrinsic monitor lock.

### Two-thread execution

```text
Thread 1 → Acquires lock
         → count++
         → Releases lock

Thread 2 → Waits for lock
         → Acquires lock
         → count++
         → Releases lock
```

### AtomicInteger vs synchronized

| AtomicInteger | synchronized |
|---|---|
| Atomic updates to one integer | Protects an entire critical section |
| No explicit lock needed | Uses intrinsic monitor |
| Good for shared counters | Good for multiple related operations |
| Does not make unrelated fields atomic together | Can protect related fields together |

---

## 6. Important AtomicInteger Methods

| Method | Meaning |
|---|---|
| `get()` | Read current value |
| `set(5)` | Set value to 5 |
| `incrementAndGet()` | Increment, then return new value |
| `getAndIncrement()` | Return old value, then increment |
| `decrementAndGet()` | Decrement, then return new value |
| `getAndDecrement()` | Return old value, then decrement |
| `addAndGet(5)` | Add 5 and return new result |
| `getAndAdd(5)` | Return old value, then add 5 |
| `compareAndSet(2, 3)` | Set to 3 only if current value is 2 |

### incrementAndGet() vs getAndIncrement()

```java
AtomicInteger count = new AtomicInteger(5);

System.out.println(count.incrementAndGet()); // 6
System.out.println(count.getAndIncrement()); // 6
System.out.println(count.get());             // 7
```

---

## 7. What Is volatile?

`volatile` is a Java keyword that provides **visibility guarantees** when threads read and write a shared variable.

Suppose:

- Thread 1 updates a variable.
- Thread 2 repeatedly reads that variable.

Without `volatile` or another synchronization mechanism, Thread 2 is not guaranteed to observe Thread 1's update.

### Normal boolean vs volatile boolean

```java
boolean running = true;
```

This is not sufficient by itself for a shared stop flag.

```java
volatile boolean running = true;
```

Now volatile reads and writes establish the necessary visibility for this simple communication pattern.

**Important:** A boolean assignment is already atomic, but atomicity and visibility are different guarantees.

---

## 8. Why Use volatile for a Shared Flag?

### Example without volatile

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

### What can happen?

| Step | Main Thread | Worker Thread |
|---|---|---|
| 1 | running = true | Reads true |
| 2 | — | Continues looping |
| 3 | running = false | May continue reading true |
| 4 | Expects worker to stop | May never exit |

The worker is not guaranteed to observe the updated value because no proper cross-thread visibility mechanism is used.

It may work sometimes, but Java does not guarantee it.

### Fix using volatile

Change only the declaration:

```java
private static volatile boolean running = true;
```

Now:

```text
Main Thread                Worker Thread
     |                           |
running = true            Reads true
     |                           |
running = false           Reads false
                                 |
                            Exits loop
```

The worker can observe the updated value through subsequent volatile reads.

### Why not use a normal int?

The same issue exists with a shared integer flag:

```java
int running = 1; // No cross-thread visibility guarantee
```

Changing it to:

```java
volatile int running = 1;
```

provides visibility.

However, for a true/false condition, a boolean is clearer.

---

## 9. volatile vs AtomicInteger — Practical Example

[Complete Java Implementation](code/VolatileVsAtomicDemo.java)

Suppose two threads each increment a shared counter 100,000 times.

Expected result:

```text
100000 + 100000 = 200000
```

### Java example

```java
private static volatile int volatileCount = 0;

private static AtomicInteger atomicCount =
    new AtomicInteger(0);

// Executed by two threads:
for (int i = 0; i < 100_000; i++) {

    volatileCount++; // Not atomic

    atomicCount.incrementAndGet(); // Atomic
}
```

### Example output

```text
Expected: 200000
Volatile: 174523
Atomic:   200000
```

This output is illustrative. The volatile result depends on how threads are scheduled; it may even equal 200000 in some runs.

### Why is volatileCount++ unsafe?

The expression performs:

```text
Read volatileCount
        |
        v
Add 1
        |
        v
Write volatileCount
```

Two threads can read the same value and overwrite each other's updates.

`volatile` guarantees visibility, not atomicity for this entire read-modify-write operation.

`AtomicInteger.incrementAndGet()` performs the increment atomically.

### Comparison

| Operation | volatile int | AtomicInteger |
|---|---|---|
| Read current value | Visible | Visible |
| Write new value | Visible | Visible |
| Increment counter atomically | No | Yes |
| Prevent lost increments | No | Yes |
| Need synchronized for simple increment? | Yes, or another safe approach | No |

---

## 10. volatile boolean vs AtomicBoolean

Both can be used for a shared true/false flag.

### volatile boolean

```java
private static volatile boolean running = true;

// Worker:
while (running) {
    // Work
}

// Controller:
running = false;
```

Use when threads simply read or assign the shared flag.

### AtomicBoolean

```java
import java.util.concurrent.atomic.AtomicBoolean;

AtomicBoolean running = new AtomicBoolean(true);

// Worker:
while (running.get()) {
    // Work
}

// Controller:
running.set(false);
```

### Comparison

| Feature | Normal boolean | volatile boolean | AtomicBoolean |
|---|---|---|---|
| Atomic true/false assignment | Yes | Yes | Yes |
| Cross-thread visibility | Not guaranteed | Yes | Yes |
| Simple shared stop flag | Unsafe without synchronization | Good | Good |
| compareAndSet() | No | No | Yes |
| Atomic conditional update | No | No | Yes |

### When would AtomicBoolean be useful?

When only one thread should successfully change a state.

```java
AtomicBoolean started =
    new AtomicBoolean(false);

if (started.compareAndSet(false, true)) {
    System.out.println("Only one thread enters");
}
```

Even if multiple threads execute this code, only one can successfully change `false` to `true`.

---

## 11. When to Use Which?

| Situation | Recommended |
|---|---|
| Single-threaded loop counter | `int` |
| Simple shared stop flag | `volatile boolean` |
| Shared flag requiring compare-and-set | `AtomicBoolean` |
| Shared counter incremented by multiple threads | `AtomicInteger` |
| Multiple related fields updated together | `synchronized` or `ReentrantLock` |
| Atomic conditional counter update | `AtomicInteger.compareAndSet()` |

---

## 12. Important AtomicInteger Limitation

AtomicInteger makes individual atomic operations safe.

But several separate operations do not automatically become atomic as one group.

### Unsafe compound operation

```java
if (count.get() < 10) {
    count.incrementAndGet();
}
```

Suppose count = 9.

| Step | Thread 1 | Thread 2 |
|---|---|---|
| 1 | Reads 9, condition true | — |
| 2 | — | Reads 9, condition true |
| 3 | Increments to 10 | — |
| 4 | — | Increments to 11 |

Both increments are atomic, but the combined condition and increment are not.

### Safe solution using compareAndSet()

```java
while (true) {

    int current = count.get();

    if (current >= 10) {
        break;
    }

    if (count.compareAndSet(current, current + 1)) {
        break;
    }
}
```

`compareAndSet(expected, updated)` updates the value only if it still equals the expected value.

If another thread changes the value, the operation fails and the loop retries.

---

## 13. Interview Explanation

### What is AtomicInteger?

"AtomicInteger is a thread-safe integer that provides atomic operations such as incrementAndGet and decrementAndGet. It prevents lost updates when multiple threads modify the same counter."

### Why use volatile?

"Volatile guarantees visibility between threads. Even if a simple boolean assignment is atomic, another thread isn't guaranteed to observe the update without synchronization. Volatile is useful for simple shared flags."

### volatile vs AtomicInteger?

"Volatile provides visibility but doesn't make count++ atomic. AtomicInteger provides atomic counter operations, so I'd use volatile for a shared stop flag and AtomicInteger for a shared counter."

### AtomicInteger vs synchronized?

"AtomicInteger is convenient for individual atomic counter operations. Synchronized protects an entire critical section and is useful when multiple related operations must happen together."

---

## 14. Quick Revision

```text
NORMAL int
    → Simple counter
    → count++ is not atomic
    → No cross-thread visibility guarantee by itself

VOLATILE
    → Visibility between threads
    → Good for simple shared flags
    → count++ still NOT atomic

ATOMIC INTEGER
    → Atomic increments/decrements
    → Thread-safe shared counter
    → No explicit synchronized block needed

ATOMIC BOOLEAN
    → Thread-safe boolean state
    → Supports compareAndSet()

SYNCHRONIZED
    → Protects critical sections
    → Mutual exclusion + visibility
    → Useful for related shared state
```

## 15. Final Takeaway

**Atomicity, visibility, and thread safety are different concepts.**

| Concept | Question It Answers |
|---|---|
| Atomicity | Can another thread interfere during this operation? |
| Visibility | Will another thread observe the updated value? |
| Thread safety | Does the overall code remain correct under concurrency? |

**Remember:**

- Normal variable: sufficient for local or properly synchronized use.
- `volatile`: visibility for simple shared state.
- `AtomicInteger`: atomic counter operations.
- `AtomicBoolean`: atomic boolean operations including compare-and-set.
- `synchronized`: protects larger critical sections.

---

[Previous: Future vs ListenableFuture vs CompletableFuture](10-future-vs-listenable-vs-completable.md)
