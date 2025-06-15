package ru.practicum.kanban.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String[] taskString) {
        super(taskString);
    }

    public List<Integer> getSubtasksId() {
        return subtasks;
    }

    public void removeSubtask(Integer id) {
        if (subtasks.contains(id)) {
            subtasks.remove(id);
        }
    }

    public void addSubtask(Integer id) {
        if (!subtasks.contains(id)) {
            subtasks.add(id);
        }
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

}
