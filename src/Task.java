import java.util.Objects;

public class Task {
    
    private String name;
    private String description;

    public Status getStatus() {
        return status;
    }

    private Status status;
    private int id;

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

    Task (String name, String description, int id) {
        
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;

    }

    public int getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
