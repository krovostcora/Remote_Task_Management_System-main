import java.time.LocalDate;

public class Task {
    private String description;
    private String category;
    private LocalDate dueDate;

    public Task(String description, String category, LocalDate dueDate) {
        this.description = description;
        this.category = category;
        this.dueDate = dueDate;
    }



    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
