import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int lastId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, TypesOfTasks> allObjects = new HashMap<>();

    public int getNewId() {
        return lastId+=1;
    }

    public void createTask (Task newTask) {

        int id = newTask.getId();
        tasks.put(id,newTask);
        allObjects.put(id,TypesOfTasks.TASK);
    }

    public void createEpic (Epic newEpic) {
        int id = newEpic.getId();
        epics.put(id,newEpic);
        allObjects.put(id,TypesOfTasks.EPIC);
    }

    public void createSubtask (Subtask newSubtask) {

        Epic parent = newSubtask.getParent();
        if (parent == null) {
            System.out.println("Такого эпика не существует. Подзадача не создана");
            return;
        }

        int id = newSubtask.getId();
        subtasks.put(id,newSubtask);
        allObjects.put(id,TypesOfTasks.SUBTASK);

        parent.addSubtask(newSubtask);
        parent.changeStatus();
    }

    public void removeAll () {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        allObjects.clear();

        lastId = 0;
    }

    public void removeById (int id) {

        TypesOfTasks type = allObjects.get(id);
        if (type == null) {
            System.out.println("Задача с таким идентификатором отсутствует");
            return;
        } else if (type == TypesOfTasks.EPIC) {
            Epic epicForRemove = epics.get(id);

            ArrayList <Subtask> subtasksOfEpic = epicForRemove.getSubtasks();
            for (Subtask subtask:subtasksOfEpic) {
                int currentId = subtask.getId();
                subtasks.remove(currentId);
                allObjects.remove(currentId);
            }

            epics.remove(id);
            allObjects.remove(id);
        } else if (type == TypesOfTasks.TASK) {
            tasks.remove(id);
        } else {
            Subtask subtaskForRemove = subtasks.get(id);
            Epic parent = subtaskForRemove.getParent();
            parent.removeSubtask(subtaskForRemove);
            subtasks.remove(id);
        }
        allObjects.remove(id);
    }

    public void updateTask(Task newTask, int id) {
        Task oldTask = tasks.get(id);
        if (oldTask == null) {
            System.out.println("Задачи с таким идентификатором не существует");
            return;
        }

        tasks.put(id, newTask);
    }

    public void updateEpic(Epic newEpic, int id) {
        Task oldTask = epics.get(id);
        if (oldTask == null) {
            System.out.println("Эпика с таким идентификатором не существует");
            return;
        }

        epics.put(id, newEpic);
    }

    public void updateSubtask(Subtask newSubtask, int id) {
        Task oldTask = subtasks.get(id);
        if (oldTask == null) {
            System.out.println("Эпика с таким идентификатором не существует");
            return;
        }

        subtasks.put(id, newSubtask);
        Epic parent = newSubtask.getParent();
        parent.changeStatus();
    }

    public ArrayList <Subtask>  getSubtasksOfEpic (Epic epic) {
        return epic.getSubtasks();
    }

}

/*Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
Методы для каждого из типа задач(Задача/Эпик/Подзадача):

d. Создание. Сам объект должен передаваться в качестве параметра.
e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
f. Удаление по идентификатору.
Дополнительные методы: a. Получение списка всех подзадач определённого эпика.
Управление статусами осуществляется по следующему правилу:
a. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
b. Для эпиков:
если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
во всех остальных случаях статус должен быть IN_PROGRESS.
Подсказк*/