
import java.util.concurrent.atomic.AtomicBoolean;

public class BooleanVisibilityDemo {

    // Visibility across threads
    private static volatile boolean running = true;

    // Atomic conditional update
    private static final AtomicBoolean started =
            new AtomicBoolean(false);

    public static void main(String[] args)
            throws InterruptedException {

        // Example 1: volatile boolean
        Thread worker = new Thread(() -> {

            while (running) {
                Thread.onSpinWait();
            }

            System.out.println("Worker stopped");
        });

        worker.start();

        Thread.sleep(100);

        // Signal worker to stop
        running = false;

        worker.join();

        // Example 2: AtomicBoolean
        Runnable task = () -> {

            if (started.compareAndSet(false, true)) {
                System.out.println(
                    Thread.currentThread().getName()
                    + " started the task"
                );
            } else {
                System.out.println(
                    Thread.currentThread().getName()
                    + " skipped the task"
                );
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
