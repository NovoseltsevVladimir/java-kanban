package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TaskManager<T extends Task> {

    int getNewId();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Subtask> getEpicSubtasks(Epic epic);

    List<T> getHistory();

    void createTask(Task newTask);

    void createEpic(Epic newEpic);

    void createSubtask(Subtask newSubtask);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task newTask);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic newEpic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void removeAll();

    void removeEpics();

    void removeSubtasks();

    void removeTasks();

    List<Task> getPrioritizedTasks();

    static boolean areTwoTasksHaveCrossing(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();

        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        boolean result = startTime1.equals(startTime2)
                || startTime1.isBefore(endTime2) && startTime1.isAfter(startTime2)
                || startTime2.isBefore(endTime1) && startTime2.isAfter(startTime1);

        return result;
    }

    static boolean isTaskHasCrossingInCollection(Task task, Map<Integer, Task> taskCollection) {

        boolean result = false;

        if (taskCollection.get(task.getId()) != null) {
            return result;
        }

        for (Task taskInCollection : taskCollection.values()) {
            if (areTwoTasksHaveCrossing(task, taskInCollection)) {
                result = true;
                break;
            }
        }

        return result;

    }
}
