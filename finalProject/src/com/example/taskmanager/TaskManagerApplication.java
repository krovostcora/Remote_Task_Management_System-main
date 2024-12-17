package com.example.taskmanager;

import java.util.*;

public class TaskManagerApplication {

    static class Task {
        int id;
        String name;
        String description;

        Task(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        @Override
        public String toString() {
            return id + ". " + name + " - " + description;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<Integer, Task> tasks = new HashMap<>();
        int taskIdCounter = 1;

        while (true) {
            System.out.println("Enter a command (ADD TASK <taskName> <taskDescription>, VIEW TASKS, DELETE TASK <taskId>, EXIT):");
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 3);

            if (parts.length < 1) continue;

            String command = parts[0].toUpperCase();

            switch (command) {
                case "ADD":
                    if (parts.length == 3 && parts[1].equalsIgnoreCase("TASK")) {
                        String[] taskDetails = parts[2].split(" ", 2);
                        if (taskDetails.length == 2) {
                            String taskName = taskDetails[0];
                            String taskDescription = taskDetails[1];
                            Task task = new Task(taskIdCounter++, taskName, taskDescription);
                            tasks.put(task.id, task);
                            System.out.println("Task added: " + task);
                        } else {
                            System.out.println("Error: Provide both task name and description.");
                        }
                    } else {
                        System.out.println("Error: Invalid ADD TASK command.");
                    }
                    break;
                case "VIEW":
                    if (parts.length == 2 && parts[1].equalsIgnoreCase("TASKS")) {
                        if (tasks.isEmpty()) {
                            System.out.println("No tasks available.");
                        } else {
                            System.out.println("Tasks:");
                            tasks.values().forEach(System.out::println);
                        }
                    } else {
                        System.out.println("Error: Invalid VIEW TASKS command.");
                    }
                    break;
                case "DELETE":
                    if (parts.length == 3 && parts[1].equalsIgnoreCase("TASK")) {
                        try {
                            int taskId = Integer.parseInt(parts[2]);
                            if (tasks.containsKey(taskId)) {
                                tasks.remove(taskId);
                                System.out.println("Task " + taskId + " deleted.");
                            } else {
                                System.out.println("Task ID not found.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid task ID.");
                        }
                    } else {
                        System.out.println("Error: Invalid DELETE TASK command.");
                    }
                    break;
                case "EXIT":
                    System.out.println("Exiting Task Manager.");
                    return;
                default:
                    System.out.println("Error: Unknown command.");
                    break;
            }
        }
    }
}
