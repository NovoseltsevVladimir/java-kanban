package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int lastId = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Subtask> getSubtasks() {
        List subtasksList = new ArrayList<>(subtasks.values());
        return subtasksList;
    }

    @Override
    public List<Epic> getEpics() {
        List epicsList = new ArrayList<>(epics.values());
        return epicsList;
    }

    @Override
    public List<Task> getTasks() {
        List tasksList = new ArrayList<>(tasks.values());
        return tasksList;
    }

    @Override
    public int getNewId() {

        return lastId += 1;
    }

    @Override
    public void createTask(Task newTask) {

        int id = getNewId();
        newTask.setId(id);
        tasks.put(id, newTask);
    }

    @Override
    public void createEpic(Epic newEpic) {

        int id = getNewId();
        newEpic.setId(id);
        epics.put(id, newEpic);
    }

    @Override
    public void createSubtask(Subtask newSubtask) {

        int parentId = newSubtask.getParentId();
        Epic parent = getEpicById(parentId);
        if (parent == null) {
            System.out.println("Такого эпика не существует. Подзадача не создана");
            return;
        }

        int id = getNewId();
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);

        parent.addSubtask(id);
        changeStatusOfEpic(parent.getId());
    }

    @Override
    public void removeAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        lastId = 0;
    }

    @Override
    public void removeEpics() {
        for (int epicId: epics.keySet()) {
            List<Integer> subtasksIdList = epics.get(epicId).getSubtasksId();
            for (int subtaskId : subtasksIdList) {
                removeFromHistory(subtaskId);
                removeSubtaskById(subtaskId);
            }
            removeFromHistory(epicId);
            removeEpicById(epicId);
        }
    }

    @Override
    public void removeSubtasks() {
        Set<Integer> epicsSet = new HashSet<>();

        for (int subtaskId : subtasks.keySet()) {
            int epicId = subtasks.get(subtaskId).getParentId();
            epicsSet.add(epicId);
            removeFromHistory(subtaskId);
            removeSubtaskById(subtaskId);
        }

        for (int epicId : epicsSet) {
            epics.get(epicId).setStatus(Status.NEW);
        }
    }

    @Override
    public void removeTasks() {
        for (int taskId : tasks.keySet()) {
            removeFromHistory(taskId);
            removeTaskById(taskId);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epicForRemove = epics.get(id);

        if (epicForRemove == null) {
            return;
        }

        List<Integer> subtasksId = epicForRemove.getSubtasksId();
        for (Integer subtaskId : subtasksId) {
            removeSubtaskById(subtaskId);
        }

        removeFromHistory(id);
        epics.remove(id);
    }

    @Override
    public void removeTaskById(int id) {

        removeFromHistory(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {

        Subtask subtaskForRemove = subtasks.get(id);
        int parentId = subtaskForRemove.getParentId();
        Epic parent = epics.get(parentId);
        parent.removeSubtask(id);
        subtasks.remove(id);
        removeFromHistory(id);

        changeStatusOfEpic(parentId);
    }

    private void changeStatusOfEpic(int id) {

        Epic epic = epics.get(id);

        boolean isStatusNew = true;
        boolean isStatusDone = true;

        List<Integer> subtasksId = epic.getSubtasksId();

        for (Integer subtaskId : subtasksId) {
            Subtask subtask = subtasks.get(subtaskId);
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
        } else if (isStatusDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task oldTask = tasks.get(id);
        if (oldTask == null) {
            System.out.println("Задачи с таким идентификатором не существует");
            return;
        }

        tasks.put(id, newTask);
    }

    @Override
    public void updateEpic(Epic newEpic) {

        int id = newEpic.getId();
        Epic oldEpic = epics.get(id);
        if (oldEpic == null) {
            System.out.println("Эпика с таким идентификатором не существует");
            return;
        }

        epics.put(id, newEpic);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        int id = newSubtask.getId();
        Subtask oldSubtask = subtasks.get(id);
        if (oldSubtask == null) {
            System.out.println("Подзадачи с таким идентификатором не существует");
            return;
        }

        subtasks.put(id, newSubtask);
        int parentId = newSubtask.getParentId();
        changeStatusOfEpic(parentId);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> subtasksList = new ArrayList<>();
        List<Integer> subtasksId = epic.getSubtasksId();

        for (int id : subtasksId) {
            subtasksList.add(subtasks.get(id));
        }

        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void removeFromHistory(int id) {
        InMemoryHistoryManager historyManagerFull = (InMemoryHistoryManager) historyManager;
        historyManagerFull.remove(id);
    }

}