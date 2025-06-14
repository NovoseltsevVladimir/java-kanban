package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String fileName = "";

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    public FileBackedTaskManager() {

    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();
    }

    @Override
    public void createSubtask(Subtask newSubtask) {
        super.createSubtask(newSubtask);
        save();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    private String getFieldsDescription() {
        return "id,type,name,status,description,epic";
    }

    public void save() {

        String lineBr = "\n";
        try (Writer fileWriter = new FileWriter(fileName)) {

            StringBuilder builder = new StringBuilder();
            builder.append(getFieldsDescription() + lineBr);

            //обработка и запись задач
            for (Task task : getTasks()) {
                builder.append(task.toString() + lineBr);
            }

            for (Epic epic : getEpics()) {
                builder.append(epic.toString() + lineBr);
            }

            for (Subtask subtask : getSubtasks()) {
                builder.append(subtask.toString() + lineBr);
            }

            //запись
            fileWriter.write(builder.toString());
        } catch (IOException exp) {
            try {
                throw new ManagerSaveException("Не удалось сохранить файл" + exp.getMessage());
            } catch (ManagerSaveException expection) {
                System.out.println(expection.getMessage());
            }
        }
    }

    public static FileBackedTaskManager loadFromFile(String fileName) {

        FileBackedTaskManager taskManager = new FileBackedTaskManager();

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
                String taskType = taskString[1];
                int currentId = Integer.parseInt(taskString[0]);
                maxId = Integer.max(maxId, currentId);

                if (taskType.equals("TASK")) {
                    Task task = new Task(taskString);
                    taskManager.tasks.put(currentId, task);
                } else if (taskType.equals("EPIC")) {
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

    public static class ManagerSaveException extends IOException {

        public ManagerSaveException() {
        }

        public ManagerSaveException(String message) {
            super(message);
        }
    }
}


