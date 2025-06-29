package ru.practicum.kanban.model;

import java.util.Comparator;

public class StatusComparator implements Comparator<Status> {
    @Override
    public int compare(Status o1, Status o2) {
        int compareInt1 = 0;
        int compareInt2 = 0;

        switch (o1) {
            case Status.IN_PROGRESS -> compareInt1 = 1;
            case Status.DONE -> compareInt1 = 2;
        }

        switch (o2) {
            case Status.IN_PROGRESS -> compareInt2 = 1;
            case Status.DONE -> compareInt2 = 2;
        }

        return compareInt1 - compareInt2;
    }
}
