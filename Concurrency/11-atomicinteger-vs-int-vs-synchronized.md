
# 11 — AtomicInteger vs int vs synchronized

[Home](README.md) | [Related: Thread Safety](02-thread-safety.md) | [Java Code](code/CountersDemo.java)

## 1. What Is AtomicInteger?

`AtomicInteger` is a thread-safe integer wrapper provided by `java.util.concurrent.atomic`.

It allows multiple threads to safely update a shared integer counter without losing increments.

## 2. Normal int vs AtomicInteger vs synchronized

| Feature | Normal `int` | `AtomicInteger` | `synchronized int` |
|---|---|---|---|
| Thread-safe increment | No | Yes | Yes |
| Increment | `count++` | `incrementAndGet()` | `count++` inside lock |
| Explicit lock needed | No | No | Uses intrinsic lock |
| Atomic increment | No | Yes | Yes |
| Best for | Local counters | Shared counters | Multiple related operations |

**Note:** A normal `int` can also be safely shared when all accesses are properly synchronized.

## 3. Normal int — Race Condition

```java
int count = 0;

count++; // Not atomic
```

`count++` involves three logical steps:

1. Read current value.
2. Increment value.
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

This is a race condition.

## 4. AtomicInteger — Thread-Safe Counter

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger count = new AtomicInteger(0);

count.incrementAndGet();
count.incrementAndGet();

System.out.println(count.get()); // 2
```

Each `incrementAndGet()` is atomic.

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

Both threads safely update the same counter.

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

The `synchronized` methods protect the shared counter using the object's intrinsic lock.

## 6. Important AtomicInteger Methods

| Method | Meaning |
|---|---|
| `get()` | Read current value |
| `set(5)` | Set value to 5 |
| `incrementAndGet()` | Increment; return new value |
| `getAndIncrement()` | Return old value; increment |
| `decrementAndGet()` | Decrement; return new value |
| `getAndDecrement()` | Return old value; decrement |
| `addAndGet(5)` | Add 5; return result |
| `compareAndSet(2, 3)` | Set 3 only if current value is 2 |

## 7. AtomicInteger vs volatile

| `volatile int` | `AtomicInteger` |
|---|---|
| Provides visibility | Provides visibility and atomic methods |
| `count++` is not atomic | `incrementAndGet()` is atomic |
| Useful for simple state flags | Useful for shared counters |

```java
volatile int count = 0;
count++; // Still unsafe with concurrent increments
```

## 8. When to Use Which?

| Situation | Recommended |
|---|---|
| Single-threaded loop counter | `int` |
| Multiple threads updating one counter | `AtomicInteger` |
| Shared mutable object with related fields | `synchronized` / `ReentrantLock` |
| Atomic conditional update | `compareAndSet()` |

## 9. Important Limitation

AtomicInteger makes individual atomic operations thread-safe.

However, multiple separate operations are not automatically atomic as a group.

```java
if (count.get() < 10) {
    count.incrementAndGet();
}
```

Two threads could both pass the check, causing the count to exceed 10.

For such conditional updates, use a compare-and-set loop or synchronization.

## Interview Explanation

"AtomicInteger is like a normal integer counter, but it provides atomic methods such as incrementAndGet and decrementAndGet. If multiple threads update the same shared counter, AtomicInteger prevents lost updates without requiring explicit synchronized blocks. For more complex updates involving multiple related variables, I'd use synchronization or locking."

## Quick Revision

```text
int             → Normal counter
count++         → Not atomic

AtomicInteger   → Thread-safe atomic counter
incrementAndGet → Atomic increment

synchronized    → Protect critical section using a lock
volatile        → Visibility, not compound atomicity
```

[Previous: Future Comparison](10-future-vs-listenable-vs-completable.md)
