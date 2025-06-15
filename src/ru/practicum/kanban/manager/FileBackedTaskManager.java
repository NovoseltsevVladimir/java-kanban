package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String FIELDS_DESCRIPTION = "id,type,name,status,description,epic";
    String fileName = "taskManagerSave.csv";

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

    public void save() {

        String lineBr = System.lineSeparator();
        try (Writer fileWriter = new FileWriter(fileName)) {

            fileWriter.write(FIELDS_DESCRIPTION + lineBr);
            //обработка и запись задач
            for (Task task : getTasks()) {
                fileWriter.write(CSVTaskConverter.getTaskDescription(task) + lineBr);
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(CSVTaskConverter.getTaskDescription(epic) + lineBr);
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(CSVTaskConverter.getTaskDescription(subtask) + lineBr);
            }
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
        taskManager = (FileBackedTaskManager) CSVTaskConverter.restoreManagerFromFile(taskManager, fileName);

        return taskManager;
    }
}


