package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVTaskConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

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
            taskDescription += subtask.getParentId() + separator;
        } else {
            taskDescription += separator;
        }

        taskDescription += task.getStartTime() == null ? "" : task.getStartTime().format(FORMATTER) + separator
                + task.getDuration().toMinutes() + separator;

        if (taskType == TaskType.EPIC) {
            Epic epic = (Epic) task;
            LocalDateTime endTime = epic.getEndTime();
            if (endTime != null) {
                taskDescription += endTime == null ? "" : endTime.format(FORMATTER);
            }
        }
        return taskDescription;

    }

    public static Task fromStringToTask(String[] taskString) {

        int id = Integer.parseInt(taskString[0]);
        TaskType taskType = TaskType.valueOf(taskString[1]);
        String name = taskString[2];
        Status status = Status.valueOf(taskString[3]);
        String description = taskString[4];
        LocalDateTime startTime = LocalDateTime.parse(taskString[6], FORMATTER);
        Duration duration = Duration.ofMinutes(Integer.parseInt(taskString[7]));

        Task task;
        if (taskType == TaskType.TASK) {
            task = new Task(name, description);
        } else if (taskType == TaskType.EPIC) {
            Epic epic = new Epic(name, description);
            if (!taskString[8].isBlank()) {
                epic.setEndTime(LocalDateTime.parse(taskString[8], FORMATTER));
            }
            task = epic;
        } else {
            int parentId = Integer.parseInt(taskString[5]);
            task = new Subtask(name, description, parentId);
        }

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        return task;
    }
}
