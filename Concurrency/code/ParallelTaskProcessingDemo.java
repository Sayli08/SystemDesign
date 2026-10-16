
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelTaskProcessingDemo {

    static class TaskProcessor {

        private final ExecutorService pool =
            Executors.newFixedThreadPool(4);

        private final Set<String> visited =
            ConcurrentHashMap.newKeySet();

        // Number of queued and running tasks
        private final AtomicInteger outstanding =
            new AtomicInteger();

        private final CountDownLatch done =
            new CountDownLatch(1);

        private final Map<String, List<String>> graph;

        TaskProcessor(Map<String, List<String>> graph) {
            this.graph = graph;
        }

        Set<String> process(String start)
                throws InterruptedException {

            schedule(start);

            try {
                // Wait for all registered work
                done.await();

                return visited;

            } finally {
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.MINUTES);
            }
        }

        private void schedule(String taskId) {

            // Only one thread successfully claims each task
            if (!visited.add(taskId)) {
                return;
            }

            // Register work before submitting it
            outstanding.incrementAndGet();

            try {
                pool.execute(() -> {

                    try {
                        System.out.println("Processing " + taskId);

                        // A task can discover more tasks
                        List<String> nextTasks =
                            graph.getOrDefault(
                                taskId, Collections.emptyList());

                        for (String next : nextTasks) {
                            schedule(next);
                        }

                    } finally {
                        taskFinished();
                    }
                });

            } catch (RejectedExecutionException e) {
                taskFinished();
                throw e;
            }
        }

        private void taskFinished() {

            // Last outstanding task signals completion
            if (outstanding.decrementAndGet() == 0) {
                done.countDown();
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Map<String, List<String>> graph = new HashMap<>();

        graph.put("A", Arrays.asList("B", "C"));
        graph.put("B", Arrays.asList("C", "D"));
        graph.put("C", Arrays.asList("A", "D"));
        graph.put("D", Collections.emptyList());

        TaskProcessor processor = new TaskProcessor(graph);

        Set<String> result = processor.process("A");

        // Sort for deterministic display
        System.out.println(new TreeSet<>(result));

        // Expected: [A, B, C, D]
    }
}
