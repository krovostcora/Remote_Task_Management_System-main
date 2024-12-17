import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TaskManager {
    private static Map<String, List<Task>> userTasks = new HashMap<>();

    public void loadTasksFromFile(String username) {
        try {
            File file = new File(username + "_tasks.txt");
            List<Task> tasks = new ArrayList<>();

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] taskParts = line.split(";", 3);
                    if (taskParts.length == 3) {
                        String description = taskParts[0];
                        String category = taskParts[1];
                        LocalDate dueDate = LocalDate.parse(taskParts[2]);
                        tasks.add(new Task(description, category, dueDate));
                    }
                }
                br.close();
            }
            userTasks.put(username, tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void saveTasksToFile(String username) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(username + "_tasks.txt"));
            List<Task> tasks = userTasks.get(username);
            if (tasks != null) {
                for (Task task : tasks) {
                    bw.write(task.getDescription() + ";" + task.getCategory() + ";" + task.getDueDate());
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void addTask(String username, Task task) {
        userTasks.get(username).add(task);
    }

    public String getTasksAsString(String username) {
        List<Task> tasks = userTasks.get(username);
        if (tasks == null || tasks.isEmpty()) {
            return "No tasks available.";
        }

        StringBuilder taskList = new StringBuilder();
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



    // New method to remove a task by description
    public boolean removeTask(String username, String taskDescription) {
        List<Task> tasks = userTasks.get(username);
        if (tasks != null) {
            return tasks.removeIf(task -> task.getDescription().equals(taskDescription));
        }
        return false;
    }
}