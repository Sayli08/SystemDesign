
# 08 — Parallel Task Processing and Completion Tracking

[Home](README.md) | [Java Code](code/ParallelTaskProcessingDemo.java)

## 1. Problem

Process a group of tasks concurrently.

A task may discover additional tasks, and multiple workers may discover the same task.

We need to:

1. Process each task only once.
2. Execute independent tasks concurrently.
3. Handle duplicates and cycles.
4. Know when all processing is complete.

## 2. Example Task Graph

```text
        A
       / \
      B   C
       \ / \
        D   A
```

Relationships:

- A discovers B and C.
- B discovers D.
- C discovers D and A.
- D has no additional tasks.

Expected tasks processed: A, B, C, D.

## 3. Single-Threaded Solution

Use a Queue and HashSet.

```java
Queue<String> queue = new ArrayDeque<>();
Set<String> visited = new HashSet<>();

queue.offer("A");
visited.add("A");

while (!queue.isEmpty()) {

    String task = queue.poll();

    for (String next : getDependencies(task)) {

        if (visited.add(next)) {
            queue.offer(next);
        }
    }
}
```

## 4. Multithreaded Solution

| Component | Responsibility |
|---|---|
| ExecutorService | Execute independent tasks |
| ConcurrentHashMap.newKeySet() | Prevent duplicate processing |
| AtomicInteger | Track outstanding work |
| CountDownLatch | Signal completion |

## 5. Thread-Safe Deduplication

```java
Set<String> visited =
    ConcurrentHashMap.newKeySet();

if (visited.add(taskId)) {
    scheduleTask(taskId);
}
```

Only one thread can successfully add a particular task ID.

## 6. Task Completion Tracking

An empty task queue does not always mean all processing is finished.

Example:

```text
Worker 1 → Processing Task B
Task Queue → Empty
Worker 1 → Discovers Task D
Task Queue → Contains D
```

Track both queued and running tasks.

```java
AtomicInteger outstanding = new AtomicInteger();
```

Conceptually:

```text
New task registered → outstanding++
Task finishes       → outstanding--

When outstanding reaches 0:
    Signal completion
```

The task registration, execution, and completion logic must be coordinated correctly to avoid premature termination.

## 7. Why Not Just Count Executor Threads?

Threads can be idle while tasks are queued, and tasks can create more tasks.

The number of worker threads is not the same as the number of unfinished tasks.

## 8. Complexity

For V unique tasks and E dependency relationships:

| Metric | Complexity |
|---|---|
| Total graph traversal work | O(V + E) |
| Visited set | O(V) |
| Unique task scheduling | Once per task |
| Executor overhead | Depends on task count and pool size |

Parallel processing can improve elapsed time, especially when independent tasks wait on I/O.

## Interview Explanation

"I'll use a fixed thread pool to execute independent tasks concurrently. I'll maintain a concurrent visited set so each task is claimed only once. I'll also track outstanding work to detect completion correctly, since an empty queue doesn't necessarily mean every worker has finished."

[Next: Interview Revision](09-interview-revision.md)
