package ru.yandex_practicum;

import java.util.List;

public interface HistoryManager {
    void add (Task task);
    List <Task> getHistory();
}
