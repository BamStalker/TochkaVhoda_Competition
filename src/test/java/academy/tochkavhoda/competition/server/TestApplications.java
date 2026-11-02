package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import academy.tochkavhoda.competition.dto.request.AddApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.CancelApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestApplications {
    private final Gson gson = new Gson();
    private Server server;
    private String participantToken;

    @BeforeEach
    public void setUp() {
        server = new Server();
        participantToken = registerParticipantAndLogin("ivanov");
    }

    // ---------- вспомогательные методы ----------

    private String registerParticipantAndLogin(String login) {
        server.registerParticipant(gson.toJson(
                new RegisterParticipantDtoRequest("Иван", "Иванов", "ООО Ромашка", login, "password1")));
        return loginAndGetToken(login);
    }

    private String registerExpertAndLogin(String login) {
        server.registerExpert(gson.toJson(
                new RegisterExpertDtoRequest("Пётр", "Петров", Arrays.asList("математика"), login, "password1")));
        return loginAndGetToken(login);
    }

    private String loginAndGetToken(String login) {
        ServerResponse response = server.login(gson.toJson(new LoginDtoRequest(login, "password1")));
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), LoginDtoResponse.class).getToken();
    }

    private ApplicationDtoRequest application(String title) {
        return new ApplicationDtoRequest(title, "Описание проекта", Arrays.asList("математика", "информатика"), 100000);
    }

    private ServerResponse add(String token, ApplicationDtoRequest... items) {
        return server.addApplications(token, gson.toJson(new AddApplicationsDtoRequest(Arrays.asList(items))));
    }

    private ServerResponse cancel(String token, int applicationId) {
        return server.cancelApplication(token, gson.toJson(new CancelApplicationDtoRequest(applicationId)));
    }

    private List<Integer> idsOf(ServerResponse response) {
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), AddApplicationsDtoResponse.class).getApplicationIds();
    }

    private void assertError(ServerResponse response, String expectedError) {
        assertEquals(400, response.getResponseCode());
        ErrorDtoResponse error = gson.fromJson(response.getResponseData(), ErrorDtoResponse.class);
        assertEquals(expectedError, error.getError());
    }

    // ---------- добавление заявок ----------

    @Test
    public void testAddOneApplication() {
        List<Integer> ids = idsOf(add(participantToken, application("Проект 1")));
        assertEquals(1, ids.size());
    }

    @Test
    public void testAddSeveralApplicationsAtOnce() {
        List<Integer> ids = idsOf(add(participantToken, application("Проект 1"), application("Проект 2")));
        assertEquals(2, ids.size());
        assertNotEquals(ids.get(0), ids.get(1));
    }

    @Test
    public void testAddApplicationsOneByOneGivesDifferentIds() {
        int id1 = idsOf(add(participantToken, application("Проект 1"))).get(0);
        int id2 = idsOf(add(participantToken, application("Проект 2"))).get(0);
        assertNotEquals(id1, id2);
    }

    @Test
    public void testAddApplicationInvalidToken() {
        assertError(add("unknown-token", application("Проект 1")), "Недействительный токен");
    }

    @Test
    public void testAddApplicationByExpert() {
        String expertToken = registerExpertAndLogin("petrov");
        assertError(add(expertToken, application("Проект 1")), "Эта операция доступна только участнику");
    }

    @Test
    public void testAddApplicationAfterLogout() {
        server.logout(participantToken);
        assertError(add(participantToken, application("Проект 1")), "Недействительный токен");
    }

    @Test
    public void testAddApplicationsEmptyList() {
        assertError(server.addApplications(participantToken, gson.toJson(
                new AddApplicationsDtoRequest(new ArrayList<>()))), "Список заявок не должен быть пустым");
    }

    @Test
    public void testAddApplicationsInvalidJson() {
        assertError(server.addApplications(participantToken, "не json"), "Некорректный формат запроса");
        assertError(server.addApplications(participantToken, null), "Некорректный формат запроса");
    }

    @Test
    public void testAddApplicationEmptyTitle() {
        assertError(add(participantToken, new ApplicationDtoRequest("", "Описание", Arrays.asList("математика"), 100)),
                "Название заявки не должно быть пустым");
    }

    @Test
    public void testAddApplicationEmptyDescription() {
        assertError(add(participantToken, new ApplicationDtoRequest("Проект", " ", Arrays.asList("математика"), 100)),
                "Описание заявки не должно быть пустым");
    }

    @Test
    public void testAddApplicationEmptyDirections() {
        assertError(add(participantToken,
                new ApplicationDtoRequest("Проект", "Описание", Collections.emptyList(), 100)),
                "Список направлений не должен быть пустым");
    }

    @Test
    public void testAddApplicationZeroAmount() {
        assertError(add(participantToken, new ApplicationDtoRequest("Проект", "Описание", Arrays.asList("математика"), 0)),
                "Запрашиваемая сумма должна быть больше нуля");
    }

    @Test
    public void testAddApplicationNegativeAmount() {
        assertError(add(participantToken, new ApplicationDtoRequest("Проект", "Описание", Arrays.asList("математика"), -5)),
                "Запрашиваемая сумма должна быть больше нуля");
    }

    @Test
    public void testAddApplicationsAllOrNothing() {
        ApplicationDtoRequest bad = new ApplicationDtoRequest("", "Описание", Arrays.asList("математика"), 100);
        assertError(add(participantToken, application("Проект 1"), bad), "Название заявки не должно быть пустым");

        // первая заявка не должна была сохраниться: следующая получает id 1, а отмена id 1... проверим через новую
        int id = idsOf(add(participantToken, application("Проект 2"))).get(0);
        assertEquals(1, id);
    }

    // ---------- отмена заявок ----------

    @Test
    public void testCancelApplication() {
        int id = idsOf(add(participantToken, application("Проект 1"))).get(0);
        ServerResponse response = cancel(participantToken, id);
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testCancelApplicationTwice() {
        int id = idsOf(add(participantToken, application("Проект 1"))).get(0);
        cancel(participantToken, id);
        assertError(cancel(participantToken, id), "Заявка не найдена");
    }

    @Test
    public void testCancelUnknownApplication() {
        assertError(cancel(participantToken, 999), "Заявка не найдена");
    }

    @Test
    public void testCancelForeignApplication() {
        int id = idsOf(add(participantToken, application("Проект 1"))).get(0);
        String otherToken = registerParticipantAndLogin("sidorov");
        assertError(cancel(otherToken, id), "Нельзя отменить чужую заявку");
        // у владельца заявка осталась
        assertEquals(200, cancel(participantToken, id).getResponseCode());
    }

    @Test
    public void testCancelApplicationByExpert() {
        int id = idsOf(add(participantToken, application("Проект 1"))).get(0);
        String expertToken = registerExpertAndLogin("petrov");
        assertError(cancel(expertToken, id), "Эта операция доступна только участнику");
    }

    @Test
    public void testCancelApplicationInvalidToken() {
        assertError(cancel("unknown-token", 1), "Недействительный токен");
    }
}
