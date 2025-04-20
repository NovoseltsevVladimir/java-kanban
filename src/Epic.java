import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList <Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasks;
    }

    public void removeSubtask (Integer id) {
        if (subtasks.contains(id)) {
            subtasks.remove(id);
        }
    }

    public void addSubtask (Integer id) {
        if (!subtasks.contains(id)) {
            subtasks.add(id);
        }
    }

}
