import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TaskManager {
    // Map to store tasks for each user: key - username, value - list of tasks
    private static Map<String, List<Task>> userTasks = new HashMap<>();

    // Method to load tasks from a user's task file
    public void loadTasksFromFile(String username) {
        try {
            File file = new File(username + "_tasks.txt");
            List<Task> tasks = new ArrayList<>();

            if (file.exists()) {
                // Open the file to read existing tasks
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                // Read each line (task) in the file
                while ((line = br.readLine()) != null) {
                    String[] taskParts = line.split(";", 3);
                    if (taskParts.length == 3) {
                        // Parse task information: description, category, and due date
                        String description = taskParts[0];
                        String category = taskParts[1];
                        LocalDate dueDate = LocalDate.parse(taskParts[2]);
                        tasks.add(new Task(description, category, dueDate));
                    }
                }
                br.close();
            }

            // Store tasks in the userTasks map
            userTasks.put(username, tasks);
        } catch (IOException e) {
            e.printStackTrace(); // Print any I/O errors
        }
    }

    // Method to save a user's tasks back to their task file
    public void saveTasksToFile(String username) {
        try {
            // Open the user's task file to write tasks
            BufferedWriter bw = new BufferedWriter(new FileWriter(username + "_tasks.txt"));
            List<Task> tasks = userTasks.get(username);

            if (tasks != null) {
                // Write each task's information to the file
                for (Task task : tasks) {
                    bw.write(task.getDescription() + ";" + task.getCategory() + ";" + task.getDueDate());
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle any I/O exceptions
        }
    }

    // Method to add a task to a user's task list
    public void addTask(String username, Task task) {
        // Get the user's task list and add the new task
        userTasks.get(username).add(task);
    }

    // Method to get all tasks as a formatted string for a user
    public String getTasksAsString(String username) {
        List<Task> tasks = userTasks.get(username);

        // If no tasks exist for the user
        if (tasks == null || tasks.isEmpty()) {
            return "No tasks available.";
        }

        StringBuilder taskList = new StringBuilder();

        // Iterate through each task and format it for display
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            taskList.append(i + 1)
                    .append(". [")
                    .append(task.getCategory())
                    .append("] ")
                    .append(task.getDescription());

            if (task.getDueDate() != null) {
                taskList.append(" (Due: ").append(task.getDueDate()).append(")");
            }
            taskList.append("\n");
        }

        return taskList.toString().trim();
    }

    // New method to remove a task based on its description
    public boolean removeTask(String username, String taskDescription) {
        List<Task> tasks = userTasks.get(username);

        if (tasks != null) {
            // Try to remove the task by matching its description
            return tasks.removeIf(task -> task.getDescription().equals(taskDescription));
        }

        return false;
    }
}
