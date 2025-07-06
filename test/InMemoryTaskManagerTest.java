import org.junit.jupiter.api.Test;
import ru.practicum.kanban.manager.InMemoryTaskManager;
import ru.practicum.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void checkTasksCrossings() {
        taskManager = new InMemoryTaskManager();

        //Добавить тест на проверку пересечения интервалов:
        Task task1 = new Task("Task 1", "");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(45));
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "");
        task2.setStartTime(LocalDateTime.now());
        task2.setDuration(Duration.ofMinutes(37));
        taskManager.createTask(task2);
        assertEquals(1, taskManager.getTasks().size(), "Добавлена задача с пересечением интервалов");

        task2.setStartTime(task2.getStartTime().plusMinutes(60));
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getTasks().size(), "Не добавлена задача с разными интервалами");

    }

}