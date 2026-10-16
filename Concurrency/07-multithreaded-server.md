
# 07 — Multithreaded TCP Server

[Home](README.md) | [Java Code](code/MultithreadedServerDemo.java)

## 1. Problem

Build a TCP server that supports multiple clients concurrently.

```text
                 TCP Server
                     |
                ServerSocket
                     |
                  accept()
                     |
               ExecutorService
                /     |     \
           Worker 1 Worker 2 Worker 3
               |       |       |
           Client A Client B Client C
```

## 2. Why Multithreading?

Socket operations may block while waiting for network activity.

For example:

- `accept()` waits for an incoming connection.
- `readLine()` waits for client input.

If a single thread handles all clients sequentially, one idle client could prevent others from being processed.

A thread pool allows multiple client handlers to execute concurrently.

## 3. Important Java APIs

| API | Purpose |
|---|---|
| ServerSocket(port) | Listen for TCP connections |
| accept() | Wait for a client |
| Socket | Represent connection |
| getInputStream() | Read incoming data |
| getOutputStream() | Send data |
| BufferedReader | Read buffered text |
| readLine() | Read a line; may block |
| ExecutorService | Manage client handlers |

## 4. Basic Implementation

```java
ExecutorService pool =
    Executors.newFixedThreadPool(10);

try (ServerSocket server = new ServerSocket(5000)) {

    while (true) {
        Socket client = server.accept();

        pool.submit(() -> handleClient(client));
    }
}
```

`handleClient()` runs on an available worker.

## 5. Concurrency Concepts

| Concept | Server Example |
|---|---|
| Thread | Executes client handler |
| ExecutorService | Reuses worker threads |
| Blocking | accept(), readLine() |
| Thread safety | Safe shared client/connection state |
| ConcurrentHashMap | Shared lookup of active clients |
| AtomicInteger | Track active connections |
| Synchronization | Protect related shared state |

## 6. Scalability

If the pool has 10 threads:

- Up to 10 client handlers execute simultaneously within the pool.
- Additional handler tasks wait in the queue.
- Idle clients can occupy workers while waiting for input.

For large numbers of idle connections, Java NIO Selector is an alternative to a thread-per-connection design.

## 7. Test Locally

Compile:

```bash
javac code/MultithreadedServerDemo.java
```

Start server:

```bash
java -cp code MultithreadedServerDemo
```

Connect in another terminal:

```bash
nc localhost 5000
```

Open multiple client terminals to test concurrency.

## Interview Explanation

"I'll use ServerSocket to accept connections and ExecutorService to process them concurrently. Each worker can handle a client independently. Because socket reads may block, the thread pool allows other available workers to continue processing clients. Shared application state must be protected separately."

[Next: Parallel Task Processing](08-parallel-task-processing.md)
