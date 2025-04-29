import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addAndGetHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Задача 1", "Сделать задачу 1");
        taskManager.createTask(task);
        Integer taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        ArrayList<Task> history = taskManager.getHistory();

        assertNotNull(history, "История не найдена.");
        assertEquals(1, history.size(), "Задача не добавлена в историю");
        assertEquals(savedTask, history.get(0), "Задача не добавлена в историю");

        for (int i = 0; i < 10; i++) {
            taskManager.getTaskById(taskId);
        }
        assertEquals(10, history.size(), "В истории не может быть больше 10 задач");

    }

}