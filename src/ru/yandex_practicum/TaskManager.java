package ru.yandex_practicum;

import java.util.List;
import java.util.Map;

public interface TaskManager <T extends Task> {

    int getNewId();

    List <Task> getTasks();
    List <Subtask> getSubtasks();
    List <Epic> getEpics();
    List<Subtask> getEpicSubtasks (Epic epic);
    List<T> getHistory ();

    void createTask (Task newTask);
    void createEpic (Epic newEpic);
    void createSubtask (Subtask newSubtask);

    Task getTaskById (int id);
    Subtask getSubtaskById (int id);
    Epic getEpicById (int id);

    void updateTask(Task newTask);
    void updateSubtask(Subtask newSubtask);
    void updateEpic(Epic newEpic);

    void removeTaskById (int id);
    void removeSubtaskById (int id);
    void removeEpicById (int id);
    void removeAll ();

}
