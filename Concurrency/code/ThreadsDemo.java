
import java.util.concurrent.*;

public class ThreadsDemo {
    public static void main(String[] args) throws Exception {

        Runnable task = () ->
            System.out.println("Worker running");

        Thread thread = new Thread(task);
        thread.start(); // New thread
        thread.join();  // Main waits

        ExecutorService pool =
            Executors.newFixedThreadPool(2);

        try {
            Future<Integer> result =
                pool.submit(() -> 10 + 20);

            System.out.println(result.get()); // 30
        } finally {
            pool.shutdown();
        }
    }
}
