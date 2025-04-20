import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int lastId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public int getNewId() {
        return lastId+=1;
    }

    public void createTask (Task newTask) {

        int id = getNewId();
        newTask.setId(id);
        tasks.put(id,newTask);
    }

    public void createEpic (Epic newEpic) {

        int id = getNewId();
        newEpic.setId(id);
        epics.put(id,newEpic);
    }

    public void createSubtask (Subtask newSubtask) {

        int parentId = newSubtask.getParentId();
        Epic parent = getEpicById(parentId);
        if (parent == null) {
            System.out.println("Такого эпика не существует. Подзадача не создана");
            return;
        }

        int id = getNewId();
        newSubtask.setId(id);
        subtasks.put(id,newSubtask);

        parent.addSubtask(id);
        changeStatusOfEpic(parent.getId());
    }

    public void removeAll () {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        lastId = 0;
    }

    public void removeEpicById (int id) {
        Epic epicForRemove = getEpicById(id);

        if (epicForRemove == null) {
            return;
        }

        ArrayList <Integer> subtasksId = epicForRemove.getSubtasksId();
        for (Integer subtaskId:subtasksId) {
            removeSubtaskById(subtaskId);
        }

        epics.remove(id);
    }

    public void removeTaskById (int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById (int id) {
        Subtask subtaskForRemove = getSubtaskById(id);
        int parentId = subtaskForRemove.getParentId();
        Epic parent = getEpicById(parentId);
        parent.removeSubtask(id);
        subtasks.remove(id);
    }

    public void changeStatusOfEpic (int id) {

        Epic epic = getEpicById(id);

        boolean isStatusNew = true;
        boolean isStatusDone = true;

        ArrayList <Integer> subtasksId = epic.getSubtasksId();

        for (Integer subtaskId:subtasksId) {
            Subtask subtask = getSubtaskById(subtaskId);
            Status statusSubtask = subtask.getStatus();
            if (statusSubtask != Status.NEW) {
                isStatusNew = false;
            }
            if (statusSubtask != Status.DONE) {
                isStatusDone = false;
            }
        }

        if (isStatusNew) {
            epic.setStatus(Status.NEW);
        } else if (isStatusDone){
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public Epic getEpicById (int id) {
        Epic epic = epics.get(id);
            return epics.get(id);
    }

    public Task getTaskById (int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById (int id) {
        return subtasks.get(id);
    }

    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) {
            System.out.println("Задачи с таким идентификатором не существует");
            return;
        }

        tasks.put(id, newTask);
    }

    public void updateEpic(Epic newEpic) {
        int id = newEpic.getId();
        Epic oldEpic= epics.get(id);
        if (oldEpic == null) {
            System.out.println("Эпика с таким идентификатором не существует");
            return;
        }

        epics.put(id, newEpic);
    }

    public void updateSubtask(Subtask newSubtask) {
        int id = newSubtask.getId();
        Subtask oldSubtask = subtasks.get(id);
        if (oldSubtask == null) {
            System.out.println("Эпика с таким идентификатором не существует");
            return;
        }

        subtasks.put(id, newSubtask);
        int parentId = newSubtask.getParentId();
        changeStatusOfEpic(parentId);
    }

    public ArrayList <Subtask>  getSubtasksOfEpic (Epic epic) {
        ArrayList <Subtask> subtasksList = new ArrayList<>();
        ArrayList <Integer> subtasksId = epic.getSubtasksId();

        for (int id:subtasksId) {
            subtasksList.add(getSubtaskById(id));
        }

        return subtasksList;
    }

}