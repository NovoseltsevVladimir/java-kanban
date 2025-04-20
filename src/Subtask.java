public class Subtask extends Task{

    public Epic getParent() {
        return parent;
    }

    private Epic parent;

    Subtask(String name, String description, int id, Epic parent) {
        super(name, description, id);
        this.parent = parent;
        parent.addSubtask(this);
    }

    public void setStatus (Status status) {
        if (this.getStatus() != status) {
            super.setStatus(status);
        }
        parent.changeStatus();
    }
}
