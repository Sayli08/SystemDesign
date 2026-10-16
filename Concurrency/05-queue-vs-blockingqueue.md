
# 05 — Queue vs BlockingQueue: Producer–Consumer

[Home](README.md) | [Java Code](code/BlockingQueueDemo.java)

## 1. Is BlockingQueue a Normal Queue?

Yes. Both are FIFO (First In, First Out) abstractions.

```text
Producer → [10, 20, 30] → Consumer
             FIFO
```

A BlockingQueue additionally supports thread-safe concurrent access and waiting operations.

## 2. Queue vs BlockingQueue

| Feature | ArrayDeque as Queue | ArrayBlockingQueue |
|---|---|---|
| FIFO | Yes | Yes |
| Thread-safe | No | Yes |
| Internal locking | No | Yes |
| Fixed capacity | No | Yes |
| offer() | Adds without waiting | Returns false if full |
| poll() | Removes without waiting | Returns null if empty |
| put() | Not supported | Waits if full |
| take() | Not supported | Waits if empty |

### Normal Queue

```java
Queue<Integer> queue = new ArrayDeque<>();

queue.offer(10);
queue.offer(20);

int value = queue.poll(); // 10
```

### BlockingQueue

```java
BlockingQueue<Integer> queue =
    new ArrayBlockingQueue<>(2);

queue.put(10);
queue.put(20);

int value = queue.take(); // 10
```

## 3. Producer–Consumer Example

Capacity = 2.

| Step | Producer | Queue | Consumer |
|---|---|---|---|
| 1 | put(10) | [10] | — |
| 2 | put(20) | [10,20] | — |
| 3 | put(30) | Full; producer waits | — |
| 4 | — | [20] | take() returns 10 |
| 5 | put(30) completes | [20,30] | — |

## 4. Why Is BlockingQueue Thread-Safe?

ArrayBlockingQueue internally uses a ReentrantLock and conditions to:
- Protect changes to the queue.
- Wait for space when full.
- Wait for items when empty.
- Signal threads when conditions change.

You don't need to write synchronized/wait/notifyAll around its normal put/take operations.

## 5. BlockingQueue Implementations

| Class | Structure | When to Use |
|---|---|---|
| ArrayBlockingQueue | Fixed-size array | Bounded producer–consumer |
| LinkedBlockingQueue | Linked nodes | FIFO with optional capacity |
| LinkedBlockingDeque | Linked nodes | Two-ended blocking operations |
| PriorityBlockingQueue | Priority heap | Priority-based processing |
| SynchronousQueue | No storage buffer | Direct handoff |

PriorityBlockingQueue is not FIFO by insertion order and is logically unbounded.

## 6. Key Java APIs

| Non-Waiting | Waiting |
|---|---|
| offer(x) | put(x) |
| poll() | take() |

Timed versions are also available:

```java
queue.offer(10, 2, TimeUnit.SECONDS);

Integer value =
    queue.poll(2, TimeUnit.SECONDS);
```

## 7. Manual vs Ready-Made Coordination

| wait()/notifyAll() | BlockingQueue |
|---|---|
| Maintain state yourself | Queue maintains state |
| Write synchronized blocks | Internal synchronization |
| Manually wait and signal | put/take handle waiting |
| More error-prone | Simpler |

## Interview Explanation

"BlockingQueue is a thread-safe queue with built-in synchronization. Its put method waits when a bounded queue is full, and take waits when empty. I'd prefer it over manually implementing wait and notifyAll for a standard Producer–Consumer problem."

[Next: Concurrent Collections](06-concurrent-collections.md)
