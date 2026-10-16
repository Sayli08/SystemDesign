
# 06 — Concurrent Collections and Coordination

[Home](README.md) | [Java Code](code/ConcurrentCollectionsDemo.java)

## 1. Why Concurrent Collections?

When multiple threads modify a shared collection, ordinary collections such as HashMap and HashSet are not automatically thread-safe.

Java provides concurrent collections that support safe concurrent operations.

## 2. Common APIs

| Requirement | Java API | Useful Operation |
|---|---|---|
| Shared counter | AtomicInteger | incrementAndGet() |
| Shared map | ConcurrentHashMap | putIfAbsent() |
| Shared set | ConcurrentHashMap.newKeySet() | add() |
| Producer–Consumer | BlockingQueue | put(), take() |
| Concurrent nonblocking queue | ConcurrentLinkedQueue | offer(), poll() |
| Completion coordination | CountDownLatch | await(), countDown() |
| Limited concurrency | Semaphore | acquire(), release() |
| Read-heavy shared list | CopyOnWriteArrayList | add(), get() |

## 3. HashMap vs ConcurrentHashMap

| HashMap | ConcurrentHashMap |
|---|---|
| Not thread-safe | Thread-safe individual operations |
| Suitable for single-threaded use | Supports concurrent access |
| External coordination needed for shared mutations | Atomic methods such as putIfAbsent() |
| Allows null keys and values | Does not allow null keys or values |

```java
Map<String, Integer> map =
    new ConcurrentHashMap<>();

map.putIfAbsent("taskA", 1);
```

## 4. Atomic Check-and-Insert

Suppose two threads discover the same task.

Unsafe:

```java
if (!visited.contains(taskId)) {
    visited.add(taskId);
    process(taskId);
}
```

Both threads might observe that the task is absent.

Safe:

```java
Set<String> visited =
    ConcurrentHashMap.newKeySet();

if (visited.add(taskId)) {
    process(taskId);
}
```

The concurrent set's `add()` operation is atomic.

### Example

| Thread 1 | Thread 2 | Shared Set |
|---|---|---|
| Discovers task D | Discovers task D | {A,B,C} |
| add(D) returns true | — | {A,B,C,D} |
| Processes D | add(D) returns false | {A,B,C,D} |
| — | Skips D | {A,B,C,D} |

## 5. AtomicInteger

Useful for safely updating a shared counter.

```java
AtomicInteger activeTasks = new AtomicInteger();

activeTasks.incrementAndGet();

// When a task finishes:
int remaining = activeTasks.decrementAndGet();
```

Unlike `count++`, incrementAndGet() is atomic.

## 6. CountDownLatch

Allows a thread to wait for a known number of completion events.

```java
CountDownLatch done = new CountDownLatch(3);

// Each completed worker:
done.countDown();

// Coordinator:
done.await();
```

The latch is one-shot and cannot be reset.

For dynamically discovered work, a fixed CountDownLatch alone may not be sufficient; a separate task-completion protocol may be required.

## 7. Semaphore

Limits the number of threads allowed to perform a particular operation simultaneously.

```java
Semaphore semaphore = new Semaphore(3);

semaphore.acquire();

try {
    performOperation();
} finally {
    semaphore.release();
}
```

Only three permits can be held simultaneously.

A Semaphore limits concurrency, not automatically requests per second.

## Interview Explanation

"I use concurrent collections when threads share mutable data. For duplicate prevention, I prefer atomic operations such as ConcurrentHashMap.newKeySet().add(). For counters, I use AtomicInteger. For coordinating completion or limiting concurrency, I use CountDownLatch or Semaphore."

[Next: Multithreaded Server](07-multithreaded-server.md)
