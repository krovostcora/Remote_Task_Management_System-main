import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;

public class TaskServer {
    private static final int PORT = 12345;
    private static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for clients...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String command;
                while ((command = in.readLine()) != null) { // Очікуємо нову команду
                    System.out.println("Received command: " + command);
                    handleCommand(command, out);           // Обробляємо команду
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close(); // Закриваємо сокет після завершення
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void handleCommand(String command, PrintWriter out) {
            String[] parts = command.split(" ", 2);
            String response = "";

            if (parts.length < 2 && !parts[0].equalsIgnoreCase("LOGOUT") && !parts[0].equalsIgnoreCase("EXIT")) {
                response = "Invalid command format.";
                out.println(response);
                return;
            }

            switch (parts[0].toUpperCase()) {
                case "REGISTER":
                    String[] registerDetails = parts[1].split(" ", 2);
                    if (registerDetails.length < 2) {
                        response = "Please provide both username and password.";
                    } else {
                        String username = registerDetails[0];
                        String password = registerDetails[1];
                        if (isUserRegistered(username)) {
                            response = "User " + username + " is already registered.";
                        } else {
                            registerUser(username, password);
                            taskManager.loadTasksFromFile(username);
                            response = "User " + username + " registered successfully!";
                        }
                    }
                    break;

                case "LOGIN":
                    String[] loginDetails = parts[1].split(" ", 2);
                    String loginUsername = loginDetails[0];
                    if (UserSession.isLoggedIn()) {
                        response = "Already logged in as " + UserSession.getLoggedInUser();
                    } else {
                        if (loginDetails.length < 2) {
                            response = "Please provide password.";
                        } else {
                            String password = loginDetails[1]; // Provided password
                            if (isValidUser(loginUsername, password)) {
                                UserSession.setLoggedInUser(loginUsername);
                                taskManager.loadTasksFromFile(loginUsername); // Load saved tasks
                                response = "Login successful. Welcome, " + loginUsername + "!";
                            } else {
                                response = "Invalid username or password.";
                            }
                        }
                    }
                    break;

                case "LOGOUT":
                    if (UserSession.isLoggedIn()) {
                        taskManager.saveTasksToFile(UserSession.getLoggedInUser()); // Save tasks before logging out
                        UserSession.logout();
                        response = "Logout successful.";
                    } else {
                        response = "No user logged in.";
                    }
                    break;

                case "ADD":
                    if (UserSession.isLoggedIn()) {
                        String[] taskParts = parts[1].split(";", 3);
                        if (taskParts.length < 3) {
                            response = "Invalid task format. Use: ADD description;category;dueDate";
                        } else {
                            String description = taskParts[0];
                            String category = taskParts[1];
                            LocalDate dueDate = LocalDate.parse(taskParts[2]);

                            Task task = new Task(description, category, dueDate);
                            taskManager.addTask(UserSession.getLoggedInUser(), task);
                            taskManager.saveTasksToFile(UserSession.getLoggedInUser());
                            response = "Task added successfully!";
                        }
                    } else {
                        response = "Please log in first.";
                    }
                    break;



                case "VIEW":
                    if (UserSession.isLoggedIn()) {
                        String tasks = taskManager.getTasksAsString(UserSession.getLoggedInUser());
                        out.println("Tasks for " + UserSession.getLoggedInUser() + ":");
                        out.println(tasks);
                        out.println("END");
                    } else {
                        out.println("Please log in first.");
                    }
                    break;





                case "DELETE":
                    if (parts.length > 1) {
                        String taskDescription = parts[1];
                        if (UserSession.isLoggedIn()) {
                            boolean deleted = taskManager.removeTask(UserSession.getLoggedInUser(), taskDescription);
                            if (deleted) {
                                taskManager.saveTasksToFile(UserSession.getLoggedInUser());
                                response = "Task '" + taskDescription + "' deleted successfully!";
                            } else {
                                response = "Task '" + taskDescription + "' not found.";
                            }
                        } else {
                            response = "Please log in first.";
                        }
                    } else {
                        response = "Task description is missing!";
                    }
                    break;

                // У методі handleCommand перевірте, чи закривається з'єднання
                case "EXIT":
                    response = "Goodbye!";
                    out.println(response);
                    out.println("END");
                    try {
                        socket.close(); // Закриваємо сокет лише для команди EXIT
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

            }

            out.println(response);
        }

        private boolean isUserRegistered(String username) {
            File userFile = new File("users.txt");
            if (userFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] user = line.split(" ", 2);
                        if (user[0].equals(username)) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        private void registerUser(String username, String password) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
                writer.write(username + " " + password + "\n"); // Store username and password
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean isValidUser(String username, String password) {
            File userFile = new File("users.txt");
            if (userFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] user = line.split(" ", 2);
                        if (user[0].equals(username) && user[1].equals(password)) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}