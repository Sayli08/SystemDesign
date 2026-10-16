
public class WaitNotifyDemo {

    static class MessageBox {
        private String message;
        private boolean hasMessage = false;

        public synchronized void put(String value)
                throws InterruptedException {

            while (hasMessage) {
                wait(); // Wait until slot is empty
            }

            message = value;
            hasMessage = true;
            notifyAll();
        }

        public synchronized String take()
                throws InterruptedException {

            while (!hasMessage) {
                wait(); // Wait until a message arrives
            }

            String result = message;
            message = null;
            hasMessage = false;

            notifyAll();
            return result;
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        MessageBox box = new MessageBox();

        Thread consumer = new Thread(() -> {
            try {
                System.out.println(box.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread producer = new Thread(() -> {
            try {
                box.put("Hello");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        producer.start();

        consumer.join();
        producer.join();
    }
}
