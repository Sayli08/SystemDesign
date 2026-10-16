
# 06 — Concurrent Collections and Coordination

[Home](README.md) | [Java Code](code/ConcurrentCollectionsDemo.java)

## 1. Java Concurrency Data Structures

| Requirement | Java API | Operation |
|---|---|---|
| Atomic counter | AtomicInteger | incrementAndGet() |
| Thread-safe map | ConcurrentHashMap | putIfAbsent() |
| Thread-safe set | ConcurrentHashMap.newKeySet() | add() |
| Thread-safe queue | BlockingQueue | put(), take() |
| Wait for completion events | CountDownLatch | await(), countDown() |
| Limit concurrent access | Semaphore | acquire(), release() |
| Read-heavy concurrent list | CopyOnWriteArrayList | add(), get() |

## 2. HashMap vs ConcurrentHashMap

| HashMap | ConcurrentHashMap |
|---|---|
| Not thread-safe | Thread-safe operations |
| Single-threaded/external lock | Concurrent access |
| No atomic putIfAbsent guarantee | Atomic putIfAbsent |

```java
Map<String, Integer> map =
    new ConcurrentHashMap<>();

map.putIfAbsent("A", 10);
```

Important: A thread-safe map doesn't automatically make a sequence of separate map operations atomic.

## 3. MongoDB Crawler — Atomic Visited Set

Unsafe:

```java
if (!visited.contains(url)) {
    visited.add(url);
    submit(url);
}
```

Two threads might both see the URL as absent.

Safe:

```java
Set<String> visited =
    ConcurrentHashMap.newKeySet();

if (visited.add(url)) {
    submit(url);
}
```

### Example

| Thread 1 | Thread 2 | visited |
|---|---|---|
| Discovers D | Discovers D | {A,B,C} |
| add(D) = true | — | {A,B,C,D} |
| Schedules D | add(D) = false | {A,B,C,D} |
| — | Skips D | {A,B,C,D} |

## 4. AtomicInteger

```java
AtomicInteger outstanding =
    new AtomicInteger();

outstanding.incrementAndGet();

if (outstanding.decrementAndGet() == 0) {
    // Last outstanding task finished
}
```

## 5. CountDownLatch

Used when a thread needs to wait for a known number of events.

```java
CountDownLatch latch =
    new CountDownLatch(3);

// Worker:
latch.countDown();

// Main:
latch.await();
```

It is a one-shot latch.

For dynamically generated tasks, a known fixed task count may not be available. Use an appropriate completion protocol.

## 6. Semaphore

Limits simultaneous permit holders.

```java
Semaphore semaphore = new Semaphore(3);

semaphore.acquire();
try {
    // At most 3 permit holders here
} finally {
    semaphore.release();
}
```

A Semaphore is not automatically a per-second rate limiter.

## Interview Explanation

"I use concurrent collections when multiple threads share data. For atomic check-and-insert, I'd use operations like ConcurrentHashMap.newKeySet().add(). For task counters, I'd use AtomicInteger, and for completion coordination I'd use CountDownLatch."

[Next: Discord TCP](07-discord-tcp.md)
