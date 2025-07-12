package ru.practicum.kanban.server;

import java.io.IOException;

public class HasCrossingsException extends IOException {

    public HasCrossingsException() {

    }

    public HasCrossingsException(String message) {
        super(message);
    }

}
