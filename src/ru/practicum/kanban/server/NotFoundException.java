package ru.practicum.kanban.server;

import java.io.IOException;

public class NotFoundException extends IOException {

    public NotFoundException() {

    }

    public NotFoundException(String message) {
        super(message);
    }

}
