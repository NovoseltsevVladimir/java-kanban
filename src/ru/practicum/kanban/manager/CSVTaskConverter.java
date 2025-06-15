package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskType;

import java.io.*;

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

    public static TaskManager restoreManagerFromFile(FileBackedTaskManager taskManager, String fileName) {

        try (Reader filereader = new FileReader(fileName); BufferedReader fileReader = new BufferedReader(filereader)) {
            boolean firstLine = true;
            int maxId = 0;

            while (fileReader.ready()) {
                if (firstLine) {
                    fileReader.readLine();
                    firstLine = false;
                    continue;
                }

                String line = fileReader.readLine();
                String[] taskString = line.split(",");
                int currentId = Integer.parseInt(taskString[0]);
                maxId = Integer.max(maxId, currentId);

                TaskType taskType = TaskType.valueOf(taskString[1]);

                if (taskType == TaskType.TASK) {
                    Task task = new Task(taskString);
                    taskManager.tasks.put(currentId, task);
                } else if (taskType == TaskType.EPIC) {
                    Epic epic = new Epic(taskString);
                    taskManager.epics.put(currentId, epic);
                } else {
                    Subtask subtask = new Subtask(taskString);
                    taskManager.subtasks.put(currentId, subtask);
                }

                taskManager.lastId = maxId;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taskManager;

    }


}
