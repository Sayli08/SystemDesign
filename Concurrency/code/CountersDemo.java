
import java.util.concurrent.atomic.AtomicInteger;

public class CountersDemo {

    static class Counter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Counter counter = new Counter();
        AtomicInteger atomic = new AtomicInteger();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
                atomic.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter.getCount()); // 2000
        System.out.println(atomic.get());        // 2000
    }
}
