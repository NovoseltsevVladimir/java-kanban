package ru.practicum.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    //если сервер корректно выполнил запрос и вернул данные — код 200 или 201;
    protected void sendText(HttpExchange exchange, String text) throws IOException {

        int code = text.isEmpty() ? 201 : 200;
        sendText(exchange, text, code);
    }

    //    если пользователь обратился к несуществующему ресурсу (например, попытался получить задачу, которой нет) — статус 404 (Not Found);
    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 404);
    }

    //    если добавляемая задача пересекается с существующими — статус 406 (Not Acceptable);
    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 406);
    }

    protected void sendServerError(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 500);
    }

    private void sendText(HttpExchange exchange, String text, int code) throws IOException {

        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        String path = exchange.getRequestURI().getPath();
        String[] parameters = path.split("/");

        switch (method) {
            case "GET":
                handleGetRequest(exchange, parameters);
                break;
            case "POST":
                handlePostRequest(exchange, parameters);
                break;
            case "DELETE":
                handleDeleteRequest(exchange, parameters);
                break;
            default:
                sendServerError(exchange, "Вид запроса не поддерживается");
        }
    }

    protected void handleGetRequest(HttpExchange httpExchange, String[] parameters) throws IOException {
        sendServerError(httpExchange, "URI не найден");
    }

    protected void handlePostRequest(HttpExchange httpExchange, String[] parameters) throws IOException {
        sendServerError(httpExchange, "URI не найден");
    }

    protected void handleDeleteRequest(HttpExchange httpExchange, String[] parameters) throws IOException {
        sendServerError(httpExchange, "URI не найден");
    }

}

