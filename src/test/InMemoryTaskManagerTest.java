package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex_practicum.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewTask() {
        Task task = new Task("Задача 1", "Сделать задачу 1");
        taskManager.createTask(task);
        Integer taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getTasks();
        int taskIndex = tasks.indexOf(savedTask);
        assertTrue(taskIndex >= 0, "Задача не сохранена");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskIndex), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Эпик 1", "Завершить все подзадачи в эпике 1");
        taskManager.createEpic(epic);
        Integer epicId = epic.getId();

        Subtask subtask1 = new Subtask("Подзадача 1.1", "Решить подзадачу 1.1", epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Решить подзадачу 1.2", epicId);
        taskManager.createSubtask(subtask2);

        Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        Assertions.assertEquals(Status.NEW, epic.getStatus(), "У эпика не определяется статус NEW");

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "У эпика не определяется статус IN_PROGRESS");

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        Assertions.assertEquals(Status.DONE, epic.getStatus(), "У эпика не определяется статус DONE");

        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");

        int epicIndex = epics.indexOf(savedEpic);
        assertTrue(epicIndex >= 0, "Эпик не сохранен");
        assertEquals(epic, epics.get(epicIndex), "Эпики не совпадают.");

        List<Integer> subtasksId = epic.getSubtasksId();
        assertNotNull(subtasksId, "Подзадачи не возвращаются.");
        assertEquals(2, subtasksId.size(), "Неверное количество подзадач.");

        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(subtask1.getId(), subtasksId.get(0), "Подзадача 1 не совпадает с добавленной.");
        Assertions.assertEquals(subtask2.getId(), subtasksId.get(1), "Подзадача 2 не совпадает с добавленной.");

    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Эпик 1", "Завершить все подзадачи в эпике 1");
        taskManager.createEpic(epic);
        Integer epicId = epic.getId();

        Subtask subtask1 = new Subtask("Подзадача 1.1", "Решить подзадачу 1.1", epicId);
        taskManager.createSubtask(subtask1);
        Integer subtask1Id = subtask1.getId();

        Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1Id);

        assertNotNull(savedSubtask1, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask1, "Задачи не совпадают.");

        List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        int indexOfSubtask1 = subtasks.indexOf(savedSubtask1);
        assertTrue(indexOfSubtask1 >= 0, "Подзадача 1 не сохранена");
        assertEquals(subtask1, subtasks.get(indexOfSubtask1), "Задачи не совпадают.");

        Subtask subtask2 = new Subtask("Подзадача 1.1", "Решить подзадачу 1.1", subtask1Id);
        taskManager.createSubtask(subtask2);

        int indexOfSubtask2 = subtasks.indexOf(subtask2);
        assertTrue(indexOfSubtask2 == -1, "Подзадача 2 не сохранена");

    }

}