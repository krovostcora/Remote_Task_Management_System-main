import java.io.*;
import java.net.*;

public class ConcurrentClientTest {
    public static void main(String[] args) throws InterruptedException {
        int numberOfClients = 10;

        for (int i = 0; i < numberOfClients; i++) {
            new Thread(() -> {
                try {
                    Socket socket = new Socket("localhost", 12345);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println("LOGIN testUser testPassword");
                    String response = in.readLine();
                    System.out.println(Thread.currentThread().getName() + ": " + response);

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
