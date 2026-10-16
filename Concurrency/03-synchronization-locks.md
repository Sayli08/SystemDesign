
# 03 — Synchronization, Locking, Deadlock

[Home](README.md) | [Java Code](code/LocksDemo.java)

## 1. Important Concepts

| Concept | Meaning |
|---|---|
| Synchronization | Coordinates threads accessing shared resources |
| Locking | Mechanism providing exclusive access |
| Critical section | Code accessing protected shared state |
| Blocking | Thread waits until it can proceed |
| Deadlock | Threads wait indefinitely for one another |

## 2. synchronized

```java
public synchronized void increment() {
    count++;
}
```

### Two Threads

```text
Thread 1 → Acquires lock
         → count++
         → Releases lock

Thread 2 → Waits for same lock
         → Acquires lock
         → count++
```

Both threads must use the same lock to protect the same shared state.

## 3. synchronized vs ReentrantLock

| Feature | synchronized | ReentrantLock |
|---|---|---|
| Mutual exclusion | Yes | Yes |
| Automatic release | Yes | No |
| tryLock() | No | Yes |
| Timed acquisition | No | Yes |
| Interruptible acquisition | No direct equivalent | Yes |
| Reentrant | Yes | Yes |

### synchronized block

```java
synchronized (lock) {
    count++;
}
```

### ReentrantLock

```java
lock.lock();

try {
    count++;
} finally {
    lock.unlock();
}
```

Always unlock in `finally`.

## 4. Deadlock

```text
Thread 1 holds Lock A → waits for Lock B
Thread 2 holds Lock B → waits for Lock A
```

Neither can continue.

### Prevention

- Acquire multiple locks in consistent order.
- Keep critical sections small.
- Avoid unnecessary nested locks.
- Consider `tryLock()` with timeout.

## Interview Explanation

"Locking is one way to achieve synchronization. It ensures only one thread at a time enters the critical section protected by the same lock. If another thread tries to acquire that lock, it may block until the lock is released."

[Next: Blocking](04-blocking-wait-notify.md)
