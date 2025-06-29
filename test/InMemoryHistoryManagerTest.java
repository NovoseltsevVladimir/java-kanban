import ru.practicum.kanban.manager.InMemoryTaskManager;

class InMemoryHistoryManagerTest extends TaskManagerTest {

    public InMemoryHistoryManagerTest () {
        taskManager = new InMemoryTaskManager();
    }
}