package ru.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {

    private String name;
    private String description;
    private Status status;
    private int id;

    private Duration duration = Duration.ofMinutes(0);
    private LocalDateTime startTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Task(String name, String description) {

        this.name = name;
        this.description = description;
        this.status = Status.NEW;

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

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
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

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {

        String separator = ",";

        return id + separator
                + getType() + separator
                + name + separator
                + status + separator
                + description + separator
                + (startTime == null ? "" : startTime.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))) + separator
                + duration.toMinutes();
    }

    @Override
    public int compareTo(Task o) {
        LocalDateTime startDate1 = this.getStartTime();
        LocalDateTime startDate2 = o.getStartTime();

        int result = 0;
        if (startDate1 != null && startDate2 != null) {
            if (startDate1.isBefore(startDate2)) {
                result = -1;
            } else if (startDate2.isBefore(startDate1)) {
                result = 1;
            } else {
                result = 0;
            }
        } else if (startDate2 == null) {
            result = -1;
        } else result = 1;

        return result;
    }
}
