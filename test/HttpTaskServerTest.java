import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import ru.practicum.kanban.HttpTaskServer;
import ru.practicum.kanban.manager.HistoryManager;
import ru.practicum.kanban.manager.InMemoryTaskManager;
import ru.practicum.kanban.manager.TaskManager;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.server.JsonConverter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;

    public HttpTaskServerTest() {
        try {
            taskServer = new HttpTaskServer(taskManager);
        } catch (IOException exp) {
            System.out.println("Не удалось запустить сервер " + exp.getStackTrace());
        }
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAll();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2");
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());

        // конвертируем её в JSON
        String taskJson = JsonConverter.convertTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {

        Subtask task = new Subtask("Subtask 1", "Testing subtask 1", 1);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());

        // конвертируем её в JSON
        String taskJson = JsonConverter.convertTask(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        Epic epic = new Epic("Epic 1", "Testing epic 1");
        taskManager.createEpic(epic);

        task = new Subtask("Subtask 1", "Testing subtask 1", epic.getId());
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());
        taskJson = JsonConverter.convertTask(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = taskManager.getSubtasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2");

        // конвертируем её в JSON
        String taskJson = JsonConverter.convertTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = taskManager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2");
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Некорректное количество задач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

    @Test
    void getPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1");
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());
        taskManager.createTask(task);

        Task task2 = new Task("Test 2", "Testing task 2");
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.now().minusDays(1));
        taskManager.createTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Сортированные задачи не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач");
        assertEquals(task2, prioritizedTasks.get(0), "Задачи не отсортированы");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

    @Test
    void testSubtasksCrossings() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "Testing epic 1");
        taskManager.createEpic(epic);

        Subtask task1 = new Subtask("Subtask 1", "Testing subtask 1", epic.getId());
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.now());

        Subtask task2 = new Subtask("Subtask 2", "Testing subtask 2", epic.getId());
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.now());

        String taskJson1 = JsonConverter.convertTask(task1);
        String taskJson2 = JsonConverter.convertTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request
                = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void testTasksCrossings() throws IOException, InterruptedException {

        Task task1 = new Task("Task 1", "Testing task 1");
        task1.setDuration(Duration.ofMinutes(5));
        task1.setStartTime(LocalDateTime.now());

        Task task2 = new Task("Task 2", "Testing task 2");
        task2.setDuration(Duration.ofMinutes(5));
        task2.setStartTime(LocalDateTime.now());

        String taskJson1 = JsonConverter.convertTask(task1);
        String taskJson2 = JsonConverter.convertTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request
                = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

}