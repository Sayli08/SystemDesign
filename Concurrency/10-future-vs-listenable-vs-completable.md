
# 10 — Asynchronous Results: Future vs ListenableFuture vs CompletableFuture

[Home](README.md) | [Java Code](code/FuturesDemo.java)

## 1. What Is a Future?

A Future represents the eventual result of an asynchronous task.

```java
ExecutorService pool =
    Executors.newFixedThreadPool(2);

Future<Integer> future =
    pool.submit(() -> 10 + 20);

int result = future.get(); // May block

pool.shutdown();
```

Submitting work is asynchronous, but `get()` waits when the result isn't ready.

## 2. Comparison

| Feature | Future | Guava ListenableFuture | CompletableFuture |
|---|---|---|---|
| Library | Java | Google Guava | Java |
| Async result | Yes | Yes | Yes |
| get() may block | Yes | Yes | Yes |
| Completion callback | No direct API | addListener/addCallback | thenAccept |
| Transform result | Manual | Futures.transform | thenApply |
| Chain async tasks | Manual | Guava utilities | thenCompose |
| Combine results | Manual | Guava utilities | thenCombine/allOf |
| Error handling | get exceptions | onFailure | exceptionally/handle |

## 3. Future

```java
Future<Integer> future =
    pool.submit(() -> 10 + 20);

int result = future.get();
```

Advantage: Simple.

Limitation: No built-in completion callback or composition API.

## 4. Google's Guava ListenableFuture

ListenableFuture extends Future with completion listeners.

```java
ListeningExecutorService executor =
    MoreExecutors.listeningDecorator(
        Executors.newFixedThreadPool(2)
    );

ListenableFuture<Integer> future =
    executor.submit(() -> 10 + 20);

Futures.addCallback(
    future,
    new FutureCallback<Integer>() {

        @Override
        public void onSuccess(Integer result) {
            System.out.println(result);
        }

        @Override
        public void onFailure(Throwable t) {
            System.out.println(t);
        }
    },
    MoreExecutors.directExecutor()
);

executor.shutdown();
```

Requires Google Guava dependency.

`directExecutor()` is suitable only for quick, lightweight callbacks. For potentially slow callbacks, pass a separate executor.

## 5. Java CompletableFuture

CompletableFuture supports callbacks and task composition.

```java
CompletableFuture<Integer> future =
    CompletableFuture.supplyAsync(() -> 10 + 20);

CompletableFuture<Void> handled =
    future.thenAccept(result ->
        System.out.println(result)
    );

handled.join();
```

## 6. CompletableFuture APIs

| API | Purpose |
|---|---|
| supplyAsync() | Run computation returning value |
| runAsync() | Run computation without result |
| thenApply() | Transform result |
| thenAccept() | Consume result |
| thenCompose() | Chain dependent async tasks |
| thenCombine() | Combine independent results |
| allOf() | Coordinate multiple futures |
| exceptionally() | Recover from failure |
| handle() | Handle success or failure |
| join() | Wait and retrieve result |

## 7. Example: Chain Operations

```java
CompletableFuture<Integer> future =
    CompletableFuture.supplyAsync(() -> 10)
        .thenApply(x -> x * 2)
        .thenApply(x -> x + 5);

System.out.println(future.join()); // 25
```

## 8. Which One Should I Choose?

| Scenario | Choice |
|---|---|
| Simple async task, then wait | Future |
| Existing Google Guava application | ListenableFuture |
| Async callback and chaining | CompletableFuture |
| Combine multiple independent async calls | CompletableFuture |

## 9. Blocking vs Async

```text
Submit Task
    |
    +-- Future.get()
    |       → Calling thread may block
    |
    +-- ListenableFuture callback
    |       → Runs when task completes
    |
    +-- CompletableFuture.thenApply()
            → Chains computation
```

Callbacks can avoid blocking the calling thread, but the underlying worker may still perform blocking I/O.

Asynchronous execution does not automatically guarantee thread safety.

## Interview Explanation

"Future represents an asynchronous result, but calling get may block. Guava ListenableFuture adds completion callbacks, while CompletableFuture offers built-in chaining, composition, and error handling. For new Java code, I'd generally prefer CompletableFuture."
