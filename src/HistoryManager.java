import java.util.ArrayList;

public interface HistoryManager <T extends Task> {
    public void add (T task);
    public ArrayList<T> getHistory();
}
