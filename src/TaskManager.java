import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager <T extends Task> {

    public int getNewId();

    public HashMap<Integer, Task> getTasks();
    public HashMap<Integer, Subtask> getSubtasks();
    public HashMap<Integer, Epic> getEpics();
    public ArrayList<Subtask> getEpicSubtasks (Epic epic);
    public ArrayList<T> getHistory ();

    public void createTask (Task newTask);
    public void createEpic (Epic newEpic);
    public void createSubtask (Subtask newSubtask);

    public Task getTaskById (int id);
    public Subtask getSubtaskById (int id);
    public Epic getEpicById (int id);

    public void updateTask(Task newTask);
    public void updateSubtask(Subtask newSubtask);
    public void updateEpic(Epic newEpic);

    public void removeTaskById (int id);
    public void removeSubtaskById (int id);
    public void removeEpicById (int id);
    public void removeAll ();

    public void changeStatusOfEpic (int id);

}
