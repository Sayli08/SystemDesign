
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentCollectionsDemo {

    public static void main(String[] args)
            throws InterruptedException {

        Set<String> visited =
            ConcurrentHashMap.newKeySet();

        AtomicInteger scheduled = new AtomicInteger();

        // Both threads try to claim URL D.
        Runnable task = () -> {
            if (visited.add("D")) {
                scheduled.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(visited);        // [D]
        System.out.println(scheduled.get()); // 1
    }
}
