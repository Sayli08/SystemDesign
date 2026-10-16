
# 09 — Java Concurrency Quick Revision

[Home](README.md)

## Core Concepts

| Question | Short Answer |
|---|---|
| What is a thread? | An independent execution path |
| Runnable vs Callable? | Runnable returns void; Callable returns a result |
| start() vs run()? | start() launches a new thread |
| Why ExecutorService? | Reuses and manages worker threads |
| What is thread safety? | Correctness with concurrent access |
| What is a race condition? | Result depends on execution interleaving |
| What is synchronization? | Coordination of access and execution order |
| What is locking? | Mutual exclusion |
| What is blocking? | A thread waits to proceed |
| What is AtomicInteger? | Atomic integer operations |
| What is ConcurrentHashMap? | Thread-safe map with atomic methods |
| What is BlockingQueue? | Thread-safe queue with blocking operations |
| Why CountDownLatch? | Wait for completion events |
| Why Semaphore? | Limit simultaneous operations |
| Future? | Asynchronous result |
| ListenableFuture? | Future with Guava completion listeners |
| CompletableFuture? | Asynchronous computation and composition |

## Common Java Choices

| Requirement | Prefer |
|---|---|
| Counter shared across threads | AtomicInteger |
| Protect multiple shared fields | synchronized / ReentrantLock |
| Thread-safe map | ConcurrentHashMap |
| Unique task tracking | ConcurrentHashMap.newKeySet() |
| Producer–Consumer | BlockingQueue |
| Managed task execution | ExecutorService |
| Wait for completion events | CountDownLatch |
| Limit concurrent work | Semaphore |
| Async callbacks or chaining | CompletableFuture |

## Practical Design Questions

### How do you serve multiple connections?

Use a ServerSocket to accept connections and an ExecutorService to handle clients concurrently.

See [Multithreaded Server](07-multithreaded-server.md).

### How do you avoid duplicate work across threads?

Use a concurrent set and atomically claim a task with `visited.add(taskId)`.

See [Parallel Task Processing](08-parallel-task-processing.md).

### How do you know all work is complete?

Track outstanding tasks or use an appropriate completion primitive. Do not assume a temporarily empty queue means all work is finished.

### How do you coordinate producers and consumers?

Use BlockingQueue, especially `put()` and `take()`, rather than manually implementing wait/notifyAll for standard queue-based coordination.

## Concurrency Design Checklist

1. Identify concurrent tasks.
2. Identify shared mutable state.
3. Prevent race conditions.
4. Choose appropriate locks or atomic operations.
5. Identify blocking operations.
6. Define completion and shutdown behavior.
7. Handle interruptions and failures.

## Summary

"First, I identify what can execute concurrently and what state is shared. Then I choose the appropriate concurrency primitives for thread safety, coordination, and completion. ExecutorService manages workers, concurrent collections protect shared data, and blocking or asynchronous APIs help control how tasks wait and communicate."
