import java.util.TimerTask;

public class ReminderService extends TimerTask {
    private TaskManager taskManager;

    public ReminderService(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void run() {

    }
}
