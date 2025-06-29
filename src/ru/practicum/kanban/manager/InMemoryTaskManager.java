package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int lastId = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> sortedTasks = new TreeSet<>();
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

        putTask(newTask);
    }

    @Override
    public void createEpic(Epic newEpic) {

        int id = getNewId();
        newEpic.setId(id);
        putTask(newEpic);
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
        putTask(newSubtask);

        parent.addSubtask(id);
        changeStatusOfEpic(parentId);
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
        for (int epicId : epics.keySet()) {
            List<Integer> subtasksIdList = epics.get(epicId).getSubtasksId();

            subtasksIdList
                    .stream()
                    .peek(subtaskId -> subtasks.remove(subtaskId))
                    .peek(subtaskId -> historyManager.remove(subtaskId));

            Epic currentEpic = epics.get(epicId);
            currentEpic.removeAllSubtasks();
            historyManager.remove(epicId);
            epics.remove(epicId);
        }
    }

    @Override
    public void removeSubtasks() {
        Set<Integer> epicsSet = new HashSet<>();

        for (int subtaskId : subtasks.keySet()) {
            int epicId = subtasks.get(subtaskId).getParentId();
            epicsSet.add(epicId);

            Epic currentEpic = epics.get(epicId);
            currentEpic.removeSubtask(subtaskId);
            historyManager.remove(subtaskId);
        }

        epicsSet
                .stream()
                .peek(epicId -> epics.get(epicId).setStatus(Status.NEW));
    }

    @Override
    public void removeTasks() {
        tasks.keySet()
                .stream()
                .peek(taskId -> removeTaskById(taskId));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        Collections.sort(allTasks);
        sortedTasks = new TreeSet<>(allTasks);
        return allTasks;
    }

    @Override
    public void removeEpicById(int id) {
        Epic epicForRemove = epics.get(id);

        if (epicForRemove == null) {
            return;
        }

        List<Integer> subtasksId = epicForRemove.getSubtasksId();
        subtasksId
                .stream()
                .peek(subtaskId -> historyManager.remove(subtaskId))
                .peek(subtaskId -> subtasks.remove(subtaskId));

        epicForRemove.removeAllSubtasks();

        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeTaskById(int id) {

        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {

        Subtask subtaskForRemove = subtasks.get(id);
        int parentId = subtaskForRemove.getParentId();
        Epic parent = epics.get(parentId);
        parent.removeSubtask(id);
        historyManager.remove(id);
        subtasks.remove(id);

        changeStatusOfEpic(parentId);
    }

    private void changeStatusOfEpic(int id) {

        Epic epic = epics.get(id);

        List<Status> statusesList = epic.getSubtasksId()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStatus())
                .sorted(new StatusComparator())
                .collect(Collectors.toList());

        Status NewStatus = Status.NEW;

        int sizeList = statusesList.size();

        if (sizeList == 1) {
            NewStatus = statusesList.get(0);
        } else if (sizeList > 1) {
            if (statusesList.getFirst() == statusesList.getLast()) {
                NewStatus = statusesList.getFirst();
            } else if (statusesList.getFirst() != Status.DONE) {
                NewStatus = Status.IN_PROGRESS;
            }
        }

        epic.setStatus(NewStatus);
        countEpicTime(epic);
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

        putTask(newTask);
    }

    @Override
    public void updateEpic(Epic newEpic) {

        int id = newEpic.getId();
        Epic oldEpic = epics.get(id);
        if (oldEpic == null) {
            System.out.println("Эпика с таким идентификатором не существует");
            return;
        }

        putTask(newEpic);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        int id = newSubtask.getId();
        Subtask oldSubtask = subtasks.get(id);
        if (oldSubtask == null) {
            System.out.println("Подзадачи с таким идентификатором не существует");
            return;
        }

        putTask(newSubtask);
        int parentId = newSubtask.getParentId();
        changeStatusOfEpic(parentId);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {

        return epic.getSubtasksId()
                .stream()
                .map(id -> subtasks.get(id))
                .collect(Collectors.toList());

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean putTask(Task task) {

        TaskType taskType = task.getType();

        Map taskCollection = tasks;
        switch (taskType) {
            case TaskType.EPIC: {
                taskCollection = epics;
                break;
            }
            case TaskType.SUBTASK: {
                taskCollection = subtasks;
                break;
            }
        }

        boolean result = false;

        List<Task> valueList = new ArrayList<>(taskCollection.values());
        valueList.add(task);
        int currentSize = valueList.size();
        if (currentSize != 1) {
            valueList.stream().filter(taskFromList -> !TaskManager.areTwoTasksHaveCrossing(taskFromList, task));
        }

        result = currentSize == valueList.size();
        if (result) {
            taskCollection.put(task.getId(), task);
        }

        return result;
    }

    public void countEpicTime(Epic epic) {

        List<Integer> subtasksId = epic.getSubtasksId();

        List<LocalDateTime> startTimeList = subtasksId
                .stream()
                .map(taskId -> subtasks.get(taskId))
                .map(subtask -> subtask.getStartTime())
                .filter(time -> time != null)
                .sorted()
                .collect(Collectors.toList());

        List<LocalDateTime> endTimeList = subtasksId
                .stream()
                .map(taskId -> subtasks.get(taskId))
                .map(subtask -> subtask.getEndTime())
                .filter(time -> time != null)
                .sorted()
                .collect(Collectors.toList());

        LocalDateTime startTime = null;
        if (startTimeList.size() > 0) {
            startTime = startTimeList.getFirst();
        }

        LocalDateTime endTime = null;
        if (endTimeList.size() > 0) {
            endTime = endTimeList.getLast();
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        if (startTime != null && endTime != null) {
            epic.setDuration(Duration.between(startTime, endTime));
        } else {
            epic.setDuration(Duration.ofMinutes(0));
        }
    }
}