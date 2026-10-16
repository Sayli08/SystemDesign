
# 01 — Threads, Runnable, ExecutorService

[Home](README.md) | [Java Code](code/ThreadsDemo.java)

## 1. What is a Thread?

A thread is an independent path of execution within a process.

```text
Java Process
   |
   +-- Main Thread
   +-- Worker Thread 1
   +-- Worker Thread 2
```

Concurrency means tasks can make overlapping progress. Parallelism means tasks execute at the same time.

## 2. Thread vs Runnable vs Callable

| Concept | Purpose | Returns Value? |
|---|---|---|
| `Thread` | Executes work | — |
| `Runnable` | Defines a task | No |
| `Callable<T>` | Defines a task | Yes |
| `ExecutorService` | Manages worker threads | — |
| `Future<T>` | Represents an asynchronous result | Yes |

### Runnable

```java
Runnable task = () -> System.out.println("Running");

Thread thread = new Thread(task);
thread.start();
```

### Callable

```java
Callable<Integer> task = () -> 10 + 20;
```

## 3. start() vs run()

| `start()` | `run()` called directly |
|---|---|
| Starts a new thread | Normal method call |
| Runs separately | Runs on calling thread |
| JVM schedules the new thread | No new thread created |

## 4. join()

```java
thread.start();
thread.join(); // Current thread waits
```

`join()` blocks the calling thread until the target thread completes.

## 5. ExecutorService

```java
ExecutorService pool =
    Executors.newFixedThreadPool(5);

Future<Integer> future =
    pool.submit(() -> 10 + 20);

int result = future.get();

pool.shutdown();
```

### Important APIs

| API | Meaning |
|---|---|
| `execute(task)` | Submit Runnable |
| `submit(task)` | Submit and return Future |
| `future.get()` | Wait for result |
| `shutdown()` | Stop accepting new tasks |
| `shutdownNow()` | Attempt interruption/cancellation |
| `awaitTermination()` | Wait for executor shutdown to finish |

A fixed thread pool reuses workers. Its default task queue is unbounded.

## 6. When to Use What?

| Situation | Choice |
|---|---|
| One simple background task | Thread |
| Multiple managed tasks | ExecutorService |
| Task returns a value | Callable + Future |
| Need completion callbacks | CompletableFuture or ListenableFuture |

## Interview Explanation

"Runnable defines the work, while Thread executes it. For multiple tasks, I prefer ExecutorService because it reuses threads and limits the number of tasks executing concurrently."

[Next: Thread Safety](02-thread-safety.md)
