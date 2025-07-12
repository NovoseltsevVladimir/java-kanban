package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.server.HasCrossingsException;
import ru.practicum.kanban.server.NotFoundException;

import java.util.List;

public interface TaskManager<T extends Task> {

    int getNewId();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Subtask> getEpicSubtasks(Epic epic);

    List<T> getHistory();

    void createTask(Task newTask) throws HasCrossingsException;

    void createEpic(Epic newEpic);

    void createSubtask(Subtask newSubtask) throws HasCrossingsException, NotFoundException;

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task newTask) throws HasCrossingsException, NotFoundException;

    void updateSubtask(Subtask newSubtask) throws HasCrossingsException, NotFoundException;

    void updateEpic(Epic newEpic) throws NotFoundException;

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void removeAll();

    void removeEpics();

    void removeSubtasks();

    void removeTasks();

    List<Task> getPrioritizedTasks();

}
