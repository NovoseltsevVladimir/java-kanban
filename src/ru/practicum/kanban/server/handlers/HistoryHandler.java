package ru.practicum.kanban.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.kanban.manager.TaskManager;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.server.JsonConverter;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetRequest(httpExchange);
                break;
            default:
                sendServerError(httpExchange, "Неподдерживаемый тип запроса");
        }
    }

    private void handleGetRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String[] parameters = path.split("/");

        if (parameters.length == 2) {
            List<Task> historyList = taskManager.getHistory();
            String response = JsonConverter.convertTaskList(historyList);
            sendText(httpExchange, response);
        } else {
            sendServerError(httpExchange, "URI не найден");
        }
    }
}

