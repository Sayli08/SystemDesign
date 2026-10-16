
# 04 — Blocking, wait(), notify(), notifyAll()

[Home](README.md) | [Java Code](code/WaitNotifyDemo.java)

## 1. Blocking vs Synchronization vs Thread Safety

| Concept | Meaning | Example |
|---|---|---|
| Thread safety | Correctness under concurrent access | AtomicInteger |
| Synchronization | Coordination between threads | synchronized |
| Locking | Mutual exclusion | ReentrantLock |
| Blocking | Thread waits | queue.take() |

**Blocking is a behavior, not a guarantee of thread safety.**

## 2. Common Blocking Operations

| Operation | Why Thread Waits |
|---|---|
| `Thread.join()` | Another thread hasn't finished |
| `Future.get()` | Result not ready |
| `wait()` | Waiting for condition notification |
| `BlockingQueue.take()` | Queue empty |
| `BlockingQueue.put()` | Bounded queue full |
| `ServerSocket.accept()` | No incoming connection |
| `CountDownLatch.await()` | Count is not zero |

## 3. wait() and notifyAll()

| API | Purpose |
|---|---|
| `wait()` | Release object's monitor and wait |
| `notify()` | Signal one waiting thread |
| `notifyAll()` | Signal all waiting threads |
| `synchronized` | Hold monitor protecting shared state |

All three Object monitor methods require owning the same monitor.

## 4. Producer–Consumer: Manual Implementation

```java
class MessageBox {
    private String message;
    private boolean hasMessage = false;

    public synchronized void put(String value)
            throws InterruptedException {

        while (hasMessage) {
            wait(); // Wait while slot is full
        }

        message = value;
        hasMessage = true;
        notifyAll();
    }

    public synchronized String take()
            throws InterruptedException {

        while (!hasMessage) {
            wait(); // Wait while slot is empty
        }

        String result = message;
        message = null;
        hasMessage = false;

        notifyAll();
        return result;
    }
}
```

## 5. Why while Instead of if?

When a thread wakes:
- Another thread may have changed the state.
- Spurious wakeups are possible.

Always recheck the condition.

## 6. What Happens When wait() Is Called?

```text
Consumer acquires monitor
    ↓
Queue/slot empty
    ↓
Consumer calls wait()
    ↓
Releases monitor and waits
    ↓
Producer acquires monitor
    ↓
Producer adds message and calls notifyAll()
    ↓
Producer releases monitor
    ↓
Consumer reacquires monitor
    ↓
Consumer checks condition and continues
```

`notifyAll()` does not immediately release the lock.

## 7. Java Thread States

| State | Meaning |
|---|---|
| NEW | Created, not started |
| RUNNABLE | Ready/running in JVM |
| BLOCKED | Waiting to enter synchronized monitor |
| WAITING | Waiting indefinitely for another action |
| TIMED_WAITING | Waiting with timeout |
| TERMINATED | Finished |

Note: General I/O blocking does not always correspond to Java's `BLOCKED` Thread.State.

## Interview Explanation

"Blocking means a thread waits until it can continue. Synchronization coordinates threads, while locking protects critical sections. wait and notifyAll provide manual condition coordination, but don't independently guarantee thread safety."

[Next: Queue vs BlockingQueue](05-queue-vs-blockingqueue.md)
