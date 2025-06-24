package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.*;

public class CSVTaskConverter {

    public static <T extends Task> String getTaskDescription(T task) {

        String separator = ",";
        TaskType taskType = task.getType();

        String taskDescription = task.getId() + separator
                + taskType + separator
                + task.getName() + separator
                + task.getStatus() + separator
                + task.getDescription() + separator;

        if (taskType == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            taskDescription += subtask.getParentId();
        }
        return taskDescription;

    }

    public static Task fromStringToTask(String[] taskString) {

        TaskType taskType = TaskType.valueOf(taskString[1]);
        Status status = Status.valueOf(taskString[3]);
        String name = taskString[2];
        String description = taskString[4];

        Task task;
        if (taskType == TaskType.TASK) {
            task = new Task(name, description);
        } else if (taskType == TaskType.EPIC) {
            task = new Epic(name, description);
        } else {
            task = new Subtask(name, description, Integer.parseInt(taskString[5]));
        }

        task.setStatus(status);
        task.setId(Integer.parseInt(taskString[0]));

        return task;
    }
}
