import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.manager.FileBackedTaskManager;
import ru.practicum.kanban.manager.ManagerSaveException;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.server.HasCrossingsException;
import ru.practicum.kanban.server.NotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest {

    public FileBackedTaskManagerTest() {
        taskManager = new FileBackedTaskManager();
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
            task.setStartTime(LocalDateTime.now());
            task.setDuration(Duration.ofMinutes(60));

            try {
                taskManager.createTask(task);
            } catch (HasCrossingsException e) {
                assertEquals(0, 1, "Не удалось добавить задачу. Она пересекается с другими");
            }

            Epic epic = new Epic("Эпик", "Сделать задачу 2");
            epic.setStartTime(LocalDateTime.now().minusHours(3));
            epic.setDuration(Duration.ofMinutes(480));
            taskManager.createEpic(epic);

            Subtask subtask1 = new Subtask("Подзадача", "Сделать задачу 3", epic.getId());
            subtask1.setStartTime(LocalDateTime.now().minusHours(1));
            subtask1.setDuration(Duration.ofMinutes(30));
            try {
                taskManager.createSubtask(subtask1);
            } catch (HasCrossingsException e) {
                assertEquals(0, 1, "Не удалось добавить подзадачу. Она пересекается с другими");
            } catch (NotFoundException e) {
                assertEquals(0, 1, "Не удалось найти эпик для подзадачи 1");
            }

            Subtask subtask2 = new Subtask("Подзадача", "Сделать задачу 3", epic.getId());
            subtask2.setStartTime(LocalDateTime.now().minusHours(2));
            subtask2.setDuration(Duration.ofMinutes(45));
            try {
                taskManager.createSubtask(subtask2);
            } catch (HasCrossingsException e) {
                assertEquals(0, 1, "Не удалось добавить подзадачу. Она пересекается с другими");
            } catch (NotFoundException e) {
                assertEquals(0, 1, "Не удалось найти эпик для подзадачи 2");
            }

            List taskList = taskManager.getPrioritizedTasks();
            assertEquals(3, taskList.size(), "После сортировки не хватает задач");
            assertEquals(subtask2, taskList.get(0), "Сортировка работает неправильно");

            taskManager.save();
            try (Reader filereader = new FileReader(fileName); BufferedReader br = new BufferedReader(filereader)) {
                int count = 0;
                while (br.ready()) {
                    String line = br.readLine();
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

    @Test
    public void testException() {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        assertThrows(ManagerSaveException.class, () -> {
            taskManager.setFileName("");
            taskManager.saveWithException();
        }, "Выгрузка при некорректном имени файла работает без исключения");

        taskManager.setFileName("testExport");
        Assertions.assertDoesNotThrow(() -> {
            taskManager.saveWithException();
        }, "Выгрузка при корректном имени файла работает с исключением");
    }


}