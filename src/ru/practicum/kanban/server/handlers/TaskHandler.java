package ru.practicum.kanban.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.kanban.manager.TaskManager;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.exceptions.HasCrossingsException;
import ru.practicum.kanban.server.JsonConverter;
import ru.practicum.kanban.exceptions.NotFoundException;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, String[] parameters) throws IOException {

        if (parameters.length == 2) {
            sendText(httpExchange, JsonConverter.convertTaskList(taskManager.getTasks()));
        } else if (parameters.length == 3) {
            int id;

            try {
                id = Integer.parseInt(parameters[2]);
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, "Id can't restore from json. Check it");
                return;
            }

            Task task = taskManager.getTaskById(id);
            if (task != null) {
                sendText(httpExchange, JsonConverter.convertTask(task));
            } else {
                sendServerError(httpExchange, "Task can't find");
            }
        } else {
            sendServerError(httpExchange, "URI not found");
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange, String[] parameters) throws IOException {

        if (parameters.length != 2) {
            sendServerError(httpExchange, "Incorrect URI");
            return;
        }

        String body = getBodyString(httpExchange);

        Task task;
        int idFromBody;

        try {
            task = JsonConverter.parseTask(body);
            idFromBody = task.getId();
        } catch (JsonSyntaxException exp) {
            sendServerError(httpExchange, "Object can't restore from json. Check json schema");
            return;
        }

        if (taskManager.getTaskById(idFromBody) == null) {
            try {
                taskManager.createTask(task);
                sendText(httpExchange, "Id " + idFromBody + " not found. Generated new id - " + task.getId());
            } catch (HasCrossingsException e) {
                sendHasInteractions(httpExchange, "Object has interactions");
            }
        } else {
            try {
                taskManager.updateTask(task);
                sendText(httpExchange, "");
            } catch (HasCrossingsException e) {
                sendHasInteractions(httpExchange, "Object has interactions");
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "id - " + task.getId() + " not found");
            }

        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange, String[] parameters) throws IOException {

        if (parameters.length == 3) {
            int id;
            try {
                id = Integer.parseInt(parameters[2]);
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, "Id can't restore from json. Check it");
                return;
            }
            if (taskManager.getTaskById(id) != null) {
                taskManager.removeTaskById(id);
                sendText(httpExchange, "");
            } else {
                sendNotFound(httpExchange, "Id not found");
            }
        } else {
            sendNotFound(httpExchange, "URI not found");
        }
    }

}
