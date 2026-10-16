
import java.util.concurrent.*;

public class BlockingQueueDemo {

    public static void main(String[] args)
            throws InterruptedException {

        BlockingQueue<Integer> queue =
            new ArrayBlockingQueue<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    queue.put(i); // Blocks if full
                    System.out.println("Produced " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    int value = queue.take(); // Blocks if empty
                    System.out.println("Consumed " + value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
