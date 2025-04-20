import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList <Subtask> subtasks = new ArrayList<>();

    Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask (Subtask subtask) {
        if (subtasks.contains(subtask)) {
            subtasks.remove(subtask);
            changeStatus();
        }
    }

    public void changeStatus () {

        boolean isStatusNew = true;
        boolean isStatusDone = true;

        for (Subtask subtask:subtasks) {
            Status statusSubtask = subtask.getStatus();
            if (statusSubtask != Status.NEW) {
                isStatusNew = false;
            }
            if (statusSubtask != Status.DONE) {
                isStatusDone = false;
            }
        }

        if (isStatusNew) {
            setStatus(Status.NEW);
        } else if (isStatusDone){
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    public void addSubtask (Subtask subtask) {
        if (!subtasks.contains(subtask)) {
            subtasks.add(subtask);
            changeStatus();
        }
    }

}
