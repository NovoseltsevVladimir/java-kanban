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
    protected Set<Task> sortedTasks = new TreeSet<>(new TaskComparator());
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

        if (taskHasCrossings(newTask)) {
            return;
        }
        int id = getNewId();
        newTask.setId(id);
        tasks.put(id, newTask);
        sortedTasks.add(newTask);
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
        Epic parent = epics.get(parentId);
        if (parent == null) {
            return;
        }

        if (taskHasCrossings(newSubtask)) {
            return;
        }

        int id = getNewId();
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        sortedTasks.add(newSubtask);

        parent.addSubtask(id);
        changeStatusOfEpic(parentId);

    }

    @Override
    public void removeAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        sortedTasks.clear();
        lastId = 0;
    }

    @Override
    public void removeEpics() {
        for (int epicId : epics.keySet()) {
            List<Integer> subtasksIdList = epics.get(epicId).getSubtasksId();

            subtasksIdList
                    .stream()
                    .peek(subtaskId -> subtasks.remove(subtaskId))
                    .peek(subtaskId -> historyManager.remove(subtaskId))
                    .peek(subtaskId -> sortedTasks.remove(subtaskId));

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
            Subtask subtask = subtasks.get(subtaskId);
            sortedTasks.remove(subtask);
            int epicId = subtask.getParentId();
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
                .peek(taskId -> sortedTasks.remove(tasks.get(taskId)))
                .peek(taskId -> removeTaskById(taskId))
        ;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
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
                .peek(subtaskId -> sortedTasks.remove(subtasks.get(subtaskId)))
                .peek(subtaskId -> subtasks.remove(subtaskId));

        epicForRemove.removeAllSubtasks();

        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeTaskById(int id) {

        historyManager.remove(id);
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {

        Subtask subtaskForRemove = subtasks.get(id);
        int parentId = subtaskForRemove.getParentId();
        Epic parent = epics.get(parentId);
        parent.removeSubtask(id);
        historyManager.remove(id);
        sortedTasks.remove(subtasks.get(id));
        subtasks.remove(id);

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

        if (taskHasCrossings(newTask)) {
            return;
        }

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

        if (taskHasCrossings(newEpic)) {
            return;
        }

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

        if (taskHasCrossings(newSubtask)) {
            return;
        }

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

        return epic.getSubtasksId()
                .stream()
                .map(id -> subtasks.get(id))
                .collect(Collectors.toList());

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void countEpicTime(Epic epic) {

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

    private boolean areTwoTasksHaveCrossing(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();

        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        boolean result = startTime1.isEqual(startTime2)
                || startTime1.isBefore(endTime2) && startTime1.isAfter(startTime2)
                || startTime2.isBefore(endTime1) && startTime2.isAfter(startTime1);

        return result;
    }

    private <T extends Task> boolean taskHasCrossings(T task) {

        boolean result = false;

        if (task.getStartTime() == null) {
            return result;
        }

        for (Task taskInCollection : sortedTasks) {
            if (taskInCollection.equals(task)) {
                continue;
            }

            if (areTwoTasksHaveCrossing(task, taskInCollection)) {
                result = true;
                break;
            }
        }

        return result;

    }
}