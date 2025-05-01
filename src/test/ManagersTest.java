package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex_practicum.Managers;

class ManagersTest {

    @Test
    void getDefault() {
        Assertions.assertNotNull(Managers.getDefault(), "Менеджер задач не определен.");
    }

    @Test
    void getDefaultHistory() {
        Assertions.assertNotNull(Managers.getDefaultHistory(), "Менеджер истории не определен.");
    }
}