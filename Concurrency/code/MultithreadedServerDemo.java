
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultithreadedServerDemo {

    private static final ExecutorService pool =
        Executors.newFixedThreadPool(10);

    public static void main(String[] args)
            throws IOException {

        try (ServerSocket server = new ServerSocket(5000)) {

            System.out.println("Server listening on port 5000");

            while (true) {

                // Wait until a client connects
                Socket client = server.accept();

                // Handle client on a pool worker
                pool.submit(() -> handleClient(client));
            }

        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket client) {

        try (
            Socket socket = client;

            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()));

            PrintWriter writer =
                new PrintWriter(
                    socket.getOutputStream(), true)
        ) {
            String message;

            // May block while waiting for client input
            while ((message = reader.readLine()) != null) {

                System.out.println(
                    "Received from " + socket.getRemoteSocketAddress()
                    + ": " + message
                );

                writer.println("Echo: " + message);
            }

        } catch (IOException e) {
            System.out.println(
                "Connection closed: " + e.getMessage()
            );
        }
    }
}
