
# 02 — Thread Safety, Race Conditions, Atomicity

[Home](README.md) | [Java Code](code/CountersDemo.java)

## 1. What is Thread Safety?

Thread safety means shared data remains correct when multiple threads access it concurrently.

## 2. Race Condition

```java
int count = 0;
count++;
```

`count++` involves reading, incrementing, and writing.

| Step | Thread 1 | Thread 2 |
|---|---|---|
| 1 | Read 0 | |
| 2 | | Read 0 |
| 3 | Calculate 1 | Calculate 1 |
| 4 | Write 1 | Write 1 |

Expected: 2. Possible actual: 1.

This is a race condition.

## 3. Atomicity vs Visibility

| Concept | Meaning |
|---|---|
| Atomicity | Operation happens as one indivisible step |
| Visibility | A thread sees updates made by other threads |
| Mutual exclusion | Only one thread holds a particular lock at a time |

## 4. Ways to Make Code Thread-Safe

| Mechanism | Example | Use |
|---|---|---|
| synchronized | `synchronized void increment()` | Protect critical section |
| ReentrantLock | `lock.lock()` | Explicit locking |
| AtomicInteger | `incrementAndGet()` | Atomic counter |
| ConcurrentHashMap | `putIfAbsent()` | Shared map |
| Immutability | Immutable objects | Avoid shared mutation |

### synchronized

```java
public synchronized void increment() {
    count++;
}
```

### AtomicInteger

```java
AtomicInteger count = new AtomicInteger(0);

count.incrementAndGet();
int result = count.get();
```

## 5. volatile

```java
volatile boolean running = true;
```

`volatile` supports visibility and ordering, but does not make compound operations atomic.

```java
volatile int count = 0;
count++; // Not an atomic increment
```

## Interview Explanation

"Thread safety means concurrent access doesn't cause incorrect results. For a simple shared counter I can use AtomicInteger. For more complex shared state, I may need synchronized blocks, locks, or concurrent collections."

[Next: Synchronization](03-synchronization-locks.md)
