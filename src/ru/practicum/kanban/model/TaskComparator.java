package ru.practicum.kanban.model;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {

        LocalDateTime date1 = o1.getStartTime();
        LocalDateTime date2 = o2.getStartTime();

        if (date1 == null && date2 == null) {
            return 0;
        } else if (date1 == null) {
            return -1;
        } else if (date2 == null) {
            return 1;
        }

        if (date1.equals(date2)) {
            return 0;
        } else if (date1.isBefore(date2)) {
            return -1;
        } else {
            return 1;
        }

    }
}
