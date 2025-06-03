import ru.yandex_practicum.*;
import ru.yandex_practicum.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager taskManager = Managers.getDefault();
        test(taskManager);
        printAllTasks(taskManager);
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        List<Task> tasks = manager.getTasks();
        List<Epic> epics = manager.getEpics();
        List<Subtask> subtasks = manager.getSubtasks();

        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : epics) {
            System.out.println(epic);

            List<Subtask> subtaskList = manager.getEpicSubtasks((Epic) epic);
            for (Subtask task : subtaskList) {
                manager.getTaskById(task.getId());
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
    }

    public static void test(InMemoryTaskManager taskManager) {

        Task task1 = new Task("Задача 1", "Сделать задачу 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Сделать задачу 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Завершить все подзадачи в эпике 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1.1","Решить подзадачу 1.1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Решить подзадачу 1.2", epic1.getId());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Завершить все подзадачи в эпике 2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 2.1","Решить подзадачу 2.1",epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getTasks());

        System.out.println("Эпиков - " + taskManager.getEpics().size());
        System.out.println("Подзадач - " + taskManager.getSubtasks().size());
        System.out.println("Задач - " + taskManager.getTasks().size());

        System.out.println(epic1.getStatus());
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        System.out.println(epic1.getStatus());

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);
        System.out.println(epic2.getStatus());
        System.out.println(taskManager.getEpicSubtasks(epic1));

        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        System.out.println("Эпиков - " + taskManager.getEpics().size());
        System.out.println("Подзадач - " + taskManager.getSubtasks().size());
        System.out.println("Задач - " + taskManager.getTasks().size());

    }

}
