import org.junit.jupiter.api.Test;
import ru.practicum.kanban.manager.InMemoryTaskManager;
import ru.practicum.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    @Test
    void addAndRemoveAndGetHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Задача 1", "Сделать задачу 1");
        taskManager.createTask(task);
        Integer taskId = task.getId();

        Task savedTask = taskManager.getTaskById(taskId);

        List<Task> history = taskManager.getHistory();
        System.out.println(history.size());

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

}