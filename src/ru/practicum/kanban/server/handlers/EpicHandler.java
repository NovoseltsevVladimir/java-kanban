package ru.practicum.kanban.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.manager.TaskManager;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.exceptions.HasCrossingsException;
import ru.practicum.kanban.server.JsonConverter;
import ru.practicum.kanban.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        ;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, String[] parameters) throws IOException {

        if (parameters.length == 2) {
            sendText(httpExchange, JsonConverter.convertTaskList(taskManager.getEpics()));
        } else {
            int id;
            try {
                id = Integer.parseInt(parameters[2]);
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, "Id can't restore from json. Check it");
                return;
            }
            Epic task = taskManager.getEpicById(id);
            if (task == null) {
                sendServerError(httpExchange, "Epic can't find");
            } else if (parameters.length == 3) {
                sendText(httpExchange, JsonConverter.convertTask(task));
            } else {
                sendText(httpExchange, JsonConverter.convertTaskList(taskManager.getEpicSubtasks(task)));
            }
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange, String[] parameters) throws IOException {

        if (parameters.length != 2) {
            sendServerError(httpExchange, "Incorrect URI");
            return;
        }

        String body = getBodyString(httpExchange);

        Epic task;
        int idFromBody;

        try {
            task = JsonConverter.parseEpic(body);
            idFromBody = task.getId();
        } catch (JsonSyntaxException exp) {
            sendServerError(httpExchange, "Object can't restore from json. Check json schema");
            return;
        }

        if (taskManager.getTaskById(idFromBody) == null) {
            try {
                taskManager.createEpic(task);
                sendText(httpExchange, "Id " + idFromBody + " not found. Generated new id - " + task.getId());
            } catch (HasCrossingsException e) {
                sendHasInteractions(httpExchange, "Object has interactions");
            }
        } else {
            try {
                taskManager.updateEpic(task);
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
            if (taskManager.getEpicById(id) != null) {
                taskManager.removeEpicById(id);
                sendText(httpExchange, "");
            } else {
                sendNotFound(httpExchange, "Id not found");
            }
        } else {
            sendNotFound(httpExchange, "URI not found");
        }
    }
}
