
# 01 — Java Threads, Runnable, Callable & ExecutorService

[Home](README.md) | [Java Code](code/ThreadsDemo.java)

## 1. What Is a Thread?

A thread is an independent path of execution within a process.

A Java application can have multiple threads executing tasks concurrently.

```text
Java Process
    |
    +-- Main Thread
    |
    +-- Worker Thread 1
    |
    +-- Worker Thread 2
```

### Concurrency vs Parallelism

| Concurrency | Parallelism |
|---|---|
| Multiple tasks make overlapping progress | Multiple tasks execute simultaneously |
| Can work on a single CPU core | Requires simultaneous execution resources |
| Tasks may be interleaved | Tasks execute at the same instant |

**Interview explanation:**

"Concurrency means multiple tasks can make progress during overlapping periods, while parallelism means tasks actually execute at the same time."

---

## 2. Three Ways to Create and Execute Threads in Java

These are the three traditional approaches commonly discussed in Java interviews.

| Approach | How | Returns Value? | Best For |
|---|---|---|---|
| 1. Extend `Thread` | Override `run()` | No | Simple examples |
| 2. Implement `Runnable` | Pass task to `Thread` | No | Separating task from execution |
| 3. Implement `Callable` | Submit to `ExecutorService` | Yes | Tasks returning results |

### Approach 1 — Extend Thread

```java
class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Running");
    }
}

// Usage
MyThread t = new MyThread();
t.start();
```

**How it works:**

1. Create a class extending `Thread`.
2. Override `run()` to define the work.
3. Call `start()` to start a new thread.
4. The JVM invokes `run()` on that new thread.

**Limitation:** Java supports single class inheritance, so the class cannot extend another class.

### Approach 2 — Implement Runnable (Preferred for Simple Tasks)

```java
class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Running");
    }
}

// Usage
Thread t = new Thread(new MyTask());
t.start();
```

Using a lambda:

```java
Thread t = new Thread(() -> {
    System.out.println("Running");
});

t.start();
```

**How it works:**

1. `Runnable` defines the work through `run()`.
2. `Thread` executes that work.
3. `start()` starts execution on a new thread.

**Why prefer Runnable?**

It separates the task definition from the thread executing it.

The same Runnable can also be submitted to an ExecutorService.

### Approach 3 — Callable with ExecutorService

```java
import java.util.concurrent.*;

ExecutorService pool =
    Executors.newFixedThreadPool(2);

Callable<Integer> task = () -> {
    return 10 + 20;
};

Future<Integer> future = pool.submit(task);

int result = future.get(); // 30

pool.shutdown();
```

**How it works:**

1. `Callable<Integer>` defines a task returning an Integer.
2. `ExecutorService` manages the worker threads.
3. `submit()` schedules the task.
4. `Future` represents its eventual result.
5. `get()` retrieves the result, blocking if necessary.

**Important:** Callable does not create a thread itself. ExecutorService executes the task.

### Runnable vs Callable

| Feature | Runnable | Callable |
|---|---|---|
| Method | `run()` | `call()` |
| Return value | `void` | Generic `T` |
| Checked exceptions | Cannot declare checked exceptions | Can declare checked exceptions |
| Can submit to ExecutorService | Yes | Yes |
| Result from submit() | `Future<?>` | `Future<T>` |
| Lambda support | Yes | Yes |

**Interview explanation:**

"There are three traditional approaches: extending Thread, implementing Runnable, and using Callable with ExecutorService. I generally prefer Runnable or Callable because they separate the task from thread management."

---

## 3. start() vs run()

This is a very common Java interview question.

| `start()` | `run()` called directly |
|---|---|
| Starts a new thread | Regular method call |
| JVM schedules new execution | Executes on the calling thread |
| Runs asynchronously relative to caller | Runs synchronously |

### Example

```java
Thread t = new Thread(() -> {
    System.out.println(
        Thread.currentThread().getName()
    );
});

t.run();   // Executes on the current thread
t.start(); // Starts a new thread
```

Calling `run()` directly does not start another thread.

**Interview explanation:**

"`start()` starts a new thread and lets the JVM schedule its execution. Calling `run()` directly is just a normal method call on the current thread."

---

## 4. Thread Lifecycle

A thread moves through several states during execution.

```text
NEW
 |
 | start()
 v
RUNNABLE
 |
 +--> BLOCKED
 |
 +--> WAITING
 |
 +--> TIMED_WAITING
 |
 v
TERMINATED
```

| State | Meaning |
|---|---|
| `NEW` | Thread created but not started |
| `RUNNABLE` | Ready or executing in JVM |
| `BLOCKED` | Waiting to acquire an intrinsic monitor |
| `WAITING` | Waiting indefinitely for another action |
| `TIMED_WAITING` | Waiting for a specified period |
| `TERMINATED` | Execution completed |

### Examples

```java
Thread.sleep(1000); // TIMED_WAITING

thread.join();       // Calling thread may WAIT

object.wait();       // WAITING, requires monitor ownership
```

**Note:** Blocking socket I/O does not necessarily correspond to Java's `BLOCKED` thread state.

---

## 5. Important Thread APIs

| Method | Purpose |
|---|---|
| `start()` | Start a new thread |
| `run()` | Define thread work |
| `join()` | Wait for thread completion |
| `sleep(ms)` | Pause current thread |
| `interrupt()` | Request interruption |
| `isInterrupted()` | Check interrupt status |
| `currentThread()` | Get currently executing thread |
| `getName()` | Get thread name |
| `isAlive()` | Check if thread has started and not terminated |

### join()

```java
Thread t = new Thread(() -> {
    System.out.println("Working");
});

t.start();

t.join(); // Main thread waits for t

System.out.println("Finished");
```

### sleep()

```java
Thread.sleep(1000); // Sleep for approximately 1 second
```

`sleep()` does not release locks held by the thread.

### interrupt()

```java
Thread t = new Thread(() -> {
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

t.start();
t.interrupt();
```

`interrupt()` requests interruption; it does not forcibly kill a thread.

**Interview explanation:**

"`join()` waits for another thread to finish, `sleep()` pauses the current thread, and `interrupt()` requests that a thread stop waiting or otherwise respond to interruption."

---

## 6. ExecutorService — Thread Pool

Creating a new thread for every task can be expensive.

`ExecutorService` manages worker threads and executes submitted tasks.

### Example

```java
ExecutorService pool =
    Executors.newFixedThreadPool(3);

pool.submit(() -> {
    System.out.println("Task A");
});

pool.submit(() -> {
    System.out.println("Task B");
});

pool.shutdown();
```

### How it works

```text
                ExecutorService
                       |
                 Thread Pool
                /     |     \
           Worker1 Worker2 Worker3
                \     |     /
                 Submitted Tasks
```

If a pool has 3 threads, at most 3 tasks execute simultaneously within that pool.

Additional tasks wait in its task queue.

### ExecutorService APIs

| API | Purpose |
|---|---|
| `execute(Runnable)` | Execute task without Future |
| `submit(Runnable)` | Submit task; return Future |
| `submit(Callable)` | Submit task returning value |
| `shutdown()` | Stop accepting new tasks |
| `shutdownNow()` | Attempt to interrupt running tasks and return queued tasks |
| `awaitTermination()` | Wait for executor termination |
| `invokeAll()` | Submit collection of tasks and wait for completion |
| `invokeAny()` | Return one successfully completed result |

### execute() vs submit()

| `execute()` | `submit()` |
|---|---|
| Accepts Runnable | Accepts Runnable or Callable |
| Returns void | Returns Future |
| No Future for observing failures | Task failures reported through Future |

### Fixed vs Cached vs Single-Thread Executor

| Executor | Behavior | Use |
|---|---|---|
| `newFixedThreadPool(n)` | Fixed number of worker threads | Bounded execution concurrency |
| `newCachedThreadPool()` | Creates/reuses threads as needed | Short-lived tasks; can grow significantly |
| `newSingleThreadExecutor()` | One worker processes tasks sequentially | Ordered background execution |
| `newScheduledThreadPool(n)` | Runs delayed/periodic tasks | Scheduling |

**Important:** `newFixedThreadPool()` uses an unbounded task queue by default. It limits running workers, not the number of waiting tasks.

---

## 7. Future — Asynchronous Result

A Future represents a task's eventual result.

```java
ExecutorService pool =
    Executors.newFixedThreadPool(2);

Future<Integer> future =
    pool.submit(() -> 10 + 20);

// Main can do other work here.

int result = future.get(); // 30

pool.shutdown();
```

### Important Future APIs

| API | Meaning |
|---|---|
| `get()` | Wait for result |
| `get(timeout, unit)` | Wait up to timeout |
| `isDone()` | Check task completion |
| `cancel(true)` | Request cancellation/interruption |
| `isCancelled()` | Check cancellation status |

### What happens during get()?

```text
Main Thread                  Worker Thread
     |                             |
     | submit(task)                |
     |---------------------------->|
     |                             | Processing
     | get()                       |
     | WAITING                     |
     |                             | Completes
     |<----------------------------|
     | Result = 30                 |
```

**Interview explanation:**

"Future represents an asynchronous computation. I can submit a task and continue doing other work, but calling `get()` blocks if the result isn't ready."

For callbacks and composition, see [Future vs ListenableFuture vs CompletableFuture](10-future-vs-listenable-vs-completable.md).

---

## 8. Thread vs Thread Pool

| Creating Thread Directly | ExecutorService |
|---|---|
| Manually create each thread | Reuses worker threads |
| Good for small demonstrations | Better for managing multiple tasks |
| Manually manage lifecycle | Centralized lifecycle management |
| No built-in task queue | Tasks can be queued |
| Less control over overall concurrency | Pool size controls execution concurrency |

### When should I use ExecutorService?

Example: Discord-style TCP server.

```java
ExecutorService pool =
    Executors.newFixedThreadPool(10);

while (true) {
    Socket client = server.accept();

    pool.submit(() -> handleClient(client));
}
```

Each connected client can have a handler running on an available worker.

A worker may block while waiting for client input. With a fixed pool of 10, only 10 handlers can execute concurrently in that pool.

See [Discord Multithreaded TCP Server](07-discord-tcp.md).

---

## 9. Common Interview Questions

| Question | Short Answer |
|---|---|
| How do you create a thread? | Extend Thread or implement Runnable; Callable tasks can run via an executor |
| Runnable vs Callable? | Runnable returns void; Callable returns a value |
| start vs run? | start launches a thread; run directly doesn't |
| Why ExecutorService? | Reuses worker threads and manages tasks |
| What is Future? | Represents asynchronous result |
| Does Future.get() block? | Yes, if result isn't ready |
| Does shutdown() wait? | No, use awaitTermination() |
| Does sleep() release a lock? | No |
| Does wait() release a lock? | Yes, the object's monitor used for wait() |
| Does interrupt() kill a thread? | No, it's an interruption request |
| Can a Runnable run in ExecutorService? | Yes |
| Is Callable itself a thread? | No, it defines a task |

---

## 10. Final Interview Summary

"Java provides three traditional approaches commonly discussed for threaded execution: extending Thread, implementing Runnable, and using Callable with ExecutorService.

Runnable defines work without a return value, while Callable can return a result and declare checked exceptions.

For managing multiple tasks, I prefer ExecutorService because it reuses threads and controls execution concurrency. If I need a task's result, I use Future, whose get method can block until completion."

## Quick Memory Notes

```text
Thread        → Executes work
Runnable      → run()  → void
Callable      → call() → returns T
ExecutorService → Manages worker threads
Future        → Represents async result

start() → New thread
run()   → Normal method call when called directly
join()  → Wait for thread completion
sleep() → Pause current thread
get()   → Wait for future result
shutdown() → Stop accepting new tasks
```

---

[Next: Thread Safety and Race Conditions](02-thread-safety.md)
