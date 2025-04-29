import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewTask () {
        Task task = new Task("Задача 1", "Сделать задачу 1");
        taskManager.createTask(task);
        Integer taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        HashMap<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic () {
        Epic epic = new Epic("Эпик 1", "Завершить все подзадачи в эпике 1");
        taskManager.createEpic(epic);
        Integer epicId = epic.getId();

        Subtask subtask1 = new Subtask("Подзадача 1.1","Решить подзадачу 1.1", epicId);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Решить подзадачу 1.2", epicId);
        taskManager.createSubtask(subtask2);

        Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        assertEquals(Status.NEW, epic.getStatus(), "У эпика не определяется статус NEW");

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "У эпика не определяется статус IN_PROGRESS");

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(Status.DONE, epic.getStatus(), "У эпика не определяется статус DONE");

        HashMap<Integer, Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");

        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(epicId), "Эпики не совпадают.");

        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        assertNotNull(subtasksId, "Подзадачи не возвращаются.");
        assertEquals(2, subtasksId.size(), "Неверное количество подзадач.");

        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(subtask1.getId(), subtasksId.get(0), "Подзадача 1 не совпадает с добавленной.");
        assertEquals(subtask2.getId(), subtasksId.get(1), "Подзадача 2 не совпадает с добавленной.");

    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Эпик 1", "Завершить все подзадачи в эпике 1");
        taskManager.createEpic(epic);
        Integer epicId = epic.getId();

        Subtask subtask1 = new Subtask("Подзадача 1.1","Решить подзадачу 1.1", epicId);
        taskManager.createSubtask(subtask1);
        Integer subtask1Id = subtask1.getId();

        Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1Id);

        assertNotNull(savedSubtask1, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask1, "Задачи не совпадают.");

        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(subtask1Id), "Задачи не совпадают.");

        Subtask subtask2 = new Subtask("Подзадача 1.1","Решить подзадачу 1.1", subtask1Id);
        taskManager.createSubtask(subtask2);

        assertNull(subtasks.get(subtask2.getId()),"Вместо ид эпика добавлена подзадача");

    }

}