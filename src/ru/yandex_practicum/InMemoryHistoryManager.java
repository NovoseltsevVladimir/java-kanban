package ru.yandex_practicum;

import java.util.ArrayList;

public class InMemoryHistoryManager <T extends Task> implements HistoryManager {

    private final int HISTORY_LIMIT = 10;
    private ArrayList <Task> history = new ArrayList<>(HISTORY_LIMIT);

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > HISTORY_LIMIT) {
            history.remove(0);
        }
    }

    @Override
    public ArrayList <Task> getHistory() {
        return history;
    }
}
