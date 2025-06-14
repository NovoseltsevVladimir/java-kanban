import org.junit.jupiter.api.Test;
import ru.practicum.kanban.manager.FileBackedTaskManager;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {

    @Test
    void addAndRemoveAndGetHistory() {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();

        Task task = new Task("Задача 1", "Сделать задачу 1");
        taskManager.createTask(task);
        Integer taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История не найдена.");
        assertEquals(1, history.size(), "Задача не добавлена в историю");
        assertEquals(savedTask, history.get(0), "Задача не добавлена в историю");

        for (int i = 0; i < 3; i++) {
            taskManager.getTaskById(taskId);
        }

        assertEquals(1, history.size(), "Задачи в истории просмотра дублируются");

        taskManager.removeTaskById(taskId);
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Задача не удаляется из истории");

    }

    @Test
    void createBlankFile() {
        try {
            Path tempFile = Files.createTempFile("testSave", ".csv");
            File file = tempFile.toFile();
            String fileName = file.getName();
            FileBackedTaskManager taskManager = new FileBackedTaskManager(fileName);
            taskManager.save();
        } catch (IOException e) {
            assertEquals(0, 1, "Не удалось сохранить файл по причине: " + e.getMessage());
        }
    }

    @Test
    void downloadBlankFile() {
        try {
            Path tempFile = Files.createTempFile("testRestore", ".csv");
            File file = tempFile.toFile();
            String fileName = file.getAbsolutePath();

            FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileName);
        } catch (IOException e) {
            assertEquals(0, 1, "Не удалось восстановить файл по причине: " + e.getMessage());
        }
    }

    @Test
    void saveAndRestoreNotBlankFile() {

        String fileName = null;
        try {
            Path tempFile = Files.createTempFile("testSave", ".csv");
            File file = tempFile.toFile();
            fileName = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertEquals(0, 1, "Не удалось получить временный файл по причине: " + e.getMessage());
        } catch (IOException e) {
            assertEquals(0, 1, "Не удалось получить временный файл по причине: " + e.getMessage());
        }

        if (fileName != null) {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(fileName);

            Task task = new Task("Задача 1", "Сделать задачу 1");
            taskManager.createTask(task);
            Epic epic = new Epic("Эпик", "Сделать задачу 2");
            taskManager.createEpic(epic);
            Subtask subtask1 = new Subtask("Подзадача", "Сделать задачу 3", epic.getId());
            taskManager.createSubtask(subtask1);
            Subtask subtask2 = new Subtask("Подзадача", "Сделать задачу 3", epic.getId());
            taskManager.createSubtask(subtask2);

            taskManager.save();
            try (Reader filereader = new FileReader(fileName); BufferedReader br = new BufferedReader(filereader)) {
                int count = 0;
                while (br.ready()) {
                    String line = br.readLine();
                    System.out.println(line);
                    count++;
                }
                assertEquals(5, count, "В файле " + count + " строк.");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                assertEquals(0, 1, "Не удалось сохранить файл по причине: " + e.getMessage());
            } catch (IOException e) {
                assertEquals(0, 1, "Не удалось сохранить файл по причине: " + e.getMessage());
            }

            FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(fileName);
            assertEquals(1, newManager.getTasks().size(), "Tasks - не загружено");
            assertEquals(1, newManager.getEpics().size(), "Epics - не загружено");
            assertEquals(2, newManager.getSubtasks().size(), "Subtasks - не загружено");
        }
    }

}