import java.io.*;
import java.net.*;

public class TaskClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to Task Server!");

            String input;
            while (true) {
                System.out.print("Enter command: ");
                input = userInput.readLine();  // Read the command from the user
                out.println(input);            // Send the command to the server

                String serverResponse;
                // Command processing using switch-case
                switch (input) {
                    case "VIEW TASKS":  // Command to view tasks
                        int taskCount = 0;  // Counter for tasks
                        while ((serverResponse = in.readLine()) != null && !serverResponse.isEmpty()) {
                            System.out.println(serverResponse); // Print each line
                            taskCount++;
                        }
                        taskCount = taskCount - 2;
                        System.out.println("Total tasks: " + taskCount); // Display the number of tasks
                        break;

                    case "LOGOUT":  // Logout command
                        serverResponse = in.readLine();
                        System.out.println(serverResponse);
                        if (serverResponse.contains("Logout successful")) {
                            return;  // Terminate the program
                        }
                        break;

                    default:  // Other commands
                        serverResponse = in.readLine();
                        if (!input.startsWith("VIEW TASKS")) {  // Ignore duplication for VIEW TASKS
                            System.out.println(serverResponse);
                        }
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
