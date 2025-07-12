package ru.practicum.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class JsonConverter {

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        Gson gson = gsonBuilder.create();
        return gson;
    }

    public static <T extends Task> String convertTask(T task) {
        Gson gson = createGson();
        return gson.toJson(task);
    }

    public static <T extends Task> String convertTaskList(List<T> taskList) {
        Gson gson = createGson();
        return gson.toJson(taskList);
    }

    public static Task parseTask(String jsonString) {
        Gson gson = createGson();
        return gson.fromJson(jsonString, Task.class);
    }

    public static Subtask parseSubtask(String jsonString) {
        Gson gson = createGson();
        return gson.fromJson(jsonString, Subtask.class);
    }

    public static Epic parseEpic(String jsonString) {
        Gson gson = createGson();
        return gson.fromJson(jsonString, Epic.class);
    }

    public static List<Task> parseTaskList(String jsonString) {
        Gson gson = createGson();
        return gson.fromJson(jsonString, new TasksTypeToken().getType());
    }

}