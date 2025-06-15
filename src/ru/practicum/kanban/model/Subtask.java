package ru.practicum.kanban.model;

public class Subtask extends Task {

    private int parentId;

    public int getParentId() {
        return parentId;
    }

    public Subtask(String name, String description, int parentId) {
        super(name, description);
        this.parentId = parentId;
    }

    public void setStatus(Status status) {
        if (this.getStatus() != status) {
            super.setStatus(status);
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String result = super.toString();
        return result + parentId;
    }
}
