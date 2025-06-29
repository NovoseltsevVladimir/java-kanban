import ru.practicum.kanban.manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest () {
        taskManager = new InMemoryTaskManager();
    }

}