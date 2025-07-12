package ru.practicum.kanban.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.manager.TaskManager;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange, String[] parameters) throws IOException {

        if (parameters.length == 2) {//tasks
            sendText(httpExchange, JsonConverter.convertTaskList(taskManager.getSubtasks()));
        } else if (parameters.length == 3) { //tasks/{id}
            int id;

            try {
                id = Integer.parseInt(parameters[2]);
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, "Id can't restore from json. Check it");
                return;
            }

            Task task = taskManager.getSubtaskById(id);
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

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Subtask task;
        int idFromBody;

        try {
            task = JsonConverter.parseSubtask(body);
            idFromBody = task.getId();
        } catch (JsonSyntaxException exp) {
            sendServerError(httpExchange, "Object can't restore from json. Check json schema");
            return;
        }

        if (taskManager.getSubtaskById(idFromBody) == null) {
            try {
                taskManager.createSubtask(task);
                sendText(httpExchange, "Id " + idFromBody + " not found. Generated new id - " + task.getId());
            } catch (HasCrossingsException e) {
                sendHasInteractions(httpExchange, "Object has interactions");
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Epic not found");
            }
        } else {
            try {
                taskManager.updateSubtask(task);
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
            if (taskManager.getSubtaskById(id) != null) {
                taskManager.removeSubtaskById(id);
                sendText(httpExchange, "");
            } else {
                sendNotFound(httpExchange, "Id not found");
            }
        } else {
            sendNotFound(httpExchange, "URI not found");
        }
    }

}
