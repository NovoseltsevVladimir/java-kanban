package ru.practicum.kanban.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Task {

    private String name;
    private String description;
    private Status status;
    private int id;

    public Task(String name, String description) {

        this.name = name;
        this.description = description;
        this.status = Status.NEW;

    }

    public Task(String[] taskString) {

        this.id = Integer.parseInt(taskString[0]);
        this.name = taskString[2];
        String statusString = taskString[3];
        if (statusString.equals("DONE")) {
            this.status = Status.DONE;
        } else if (statusString.equals("NEW")) {
            this.status = Status.NEW;
        } else {
            this.status = Status.IN_PROGRESS;
        }

        this.description = taskString[4];
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskTypes getType () {
        return TaskTypes.TASK;
    }

    @Override
    public String toString() {

        String separator = ",";

        return id + separator
                + getType() + separator
                + name + separator
                + status + separator
                + description + separator;
    }


}
