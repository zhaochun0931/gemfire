import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class eof {

    private static final int PORT = 40511;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT + "...");
            int threadCounter = 16; // Mimicking your "Thread 16"

            while (true) {
                // 1. Wait for a new client connection
                Socket clientSocket = serverSocket.accept();

                // 2. Hand off the connection to a new worker thread
                Thread workerThread = new Thread(
                        () -> handleClient(clientSocket),
                        "Thread " + threadCounter++
                );
                workerThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        String clientIp = clientSocket.getInetAddress().getHostAddress();
        int clientPort = clientSocket.getPort();
        String threadName = Thread.currentThread().getName();

        // FIX: Changed "%" to "%s" to properly format the string
        String mockClientId = String.format("identity(%s)", clientIp);

        try {
            InputStream input = clientSocket.getInputStream();

            // 3. Attempt to read the initial header/handshake from the client
            byte[] headerBuffer = new byte[1024];
            int bytesRead = input.read(headerBuffer);

            // 4. Check for EOF (-1 means the client closed the stream gracefully or abruptly)
            if (bytesRead == -1) {
                logWarn(threadName, "Server connection from [" + mockClientId + "; port=" + clientPort + "]: connection disconnect detected by EOF.");
                logWarn(threadName, "client with id " + mockClientId + " due to: The connection has been reset while reading the header");
                return;
            }

            // If we successfully read data, we'd process the auth principal
            logInfo(threadName, "test");

            // ... normal server processing would continue here ...

        } catch (IOException e) {
            // 5. Check for forced resets (e.g., "Connection reset by peer")
            logWarn(threadName, " client with member id " + mockClientId + " due to: The connection has been reset while reading the header");
        } finally {
            // 6. Always clean up the socket
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore errors on close
            }
        }
    }

    // --- Helper methods to format logs exactly like your system ---

    private static void logInfo(String thread, String message) {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z").format(new Date());
        System.out.printf("[info %s  <ServerConnection on port %d %s> ] %s%n",
                timestamp, PORT, thread, message);
    }

    private static void logWarn(String thread, String message) {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z").format(new Date());
        System.out.printf("[warn %s  <ServerConnection on port %d %s> ] %s%n",
                timestamp, PORT, thread, message);
    }
}
