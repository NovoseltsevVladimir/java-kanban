
public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        test(taskManager);

    }

    public static void test (TaskManager taskManager) {

        Task task1 = new Task("Задача 1", "Сделать задачу 1", taskManager.getNewId());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Сделать задачу 2", taskManager.getNewId());
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Завершить все подзадачи в эпике 1", taskManager.getNewId());
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1.1","Решить подзадачу 1.1", taskManager.getNewId(), epic1);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Решить подзадачу 1.2", taskManager.getNewId(), epic1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Завершить все подзадачи в эпике 2", taskManager.getNewId());
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 2.1","Решить подзадачу 2.1", taskManager.getNewId(),epic2);
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getTasks());

        System.out.println("Эпиков - " + taskManager.getEpics().size());
        System.out.println("Подзадач - " + taskManager.getSubtasks().size());
        System.out.println("Задач - " + taskManager.getTasks().size());

        System.out.println(epic1.getStatus());
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1, subtask1.getId());
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2, subtask2.getId());
        System.out.println(epic1.getStatus());

        subtask3.setStatus(Status.DONE);
        System.out.println(epic2.getStatus());
        System.out.println(taskManager.getSubtasksOfEpic(epic1));

        taskManager.removeById(task1.getId());
        taskManager.removeById(epic1.getId());

        System.out.println("Эпиков - " + taskManager.getEpics().size());
        System.out.println("Подзадач - " + taskManager.getSubtasks().size());
        System.out.println("Задач - " + taskManager.getTasks().size());

    }

}
