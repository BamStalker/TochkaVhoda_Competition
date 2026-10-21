package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestServer {
    private final Gson gson = new Gson();
    private Server server;

    @BeforeEach
    public void setUp() {
        server = new Server();
    }

    private ServerResponse registerParticipant(String login, String password) {
        return server.registerParticipant(gson.toJson(
                new RegisterParticipantDtoRequest("Иван", "Иванов", "ООО Ромашка", login, password)));
    }

    private ServerResponse registerExpert(String login, String password) {
        return server.registerExpert(gson.toJson(
                new RegisterExpertDtoRequest("Пётр", "Петров", Arrays.asList("математика", "информатика"),
                        login, password)));
    }

    private ServerResponse login(String login, String password) {
        return server.login(gson.toJson(new LoginDtoRequest(login, password)));
    }

    private String loginAndGetToken(String login, String password) {
        ServerResponse response = login(login, password);
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), LoginDtoResponse.class).getToken();
    }

    private void assertError(ServerResponse response, String expectedError) {
        assertEquals(400, response.getResponseCode());
        ErrorDtoResponse error = gson.fromJson(response.getResponseData(), ErrorDtoResponse.class);
        assertEquals(expectedError, error.getError());
    }

    // ---------- регистрация участника ----------

    @Test
    public void testRegisterParticipant() {
        ServerResponse response = registerParticipant("ivanov", "password1");
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testRegisterParticipantDuplicateLogin() {
        registerParticipant("ivanov", "password1");
        assertError(registerParticipant("ivanov", "password2"), "Этот логин уже используется");
    }

    @Test
    public void testRegisterParticipantEmptyFirstName() {
        ServerResponse response = server.registerParticipant(gson.toJson(
                new RegisterParticipantDtoRequest("", "Иванов", "ООО Ромашка", "ivanov", "password1")));
        assertError(response, "Имя не должно быть пустым");
    }

    @Test
    public void testRegisterParticipantShortPassword() {
        assertError(registerParticipant("ivanov", "pass1"), "Пароль слишком короткий (минимум 8 символов)");
    }

    @Test
    public void testRegisterParticipantWeakPassword() {
        assertError(registerParticipant("ivanov", "passwordonly"),
                "Пароль должен содержать хотя бы одну букву и одну цифру");
    }

    @Test
    public void testRegisterParticipantInvalidJson() {
        assertError(server.registerParticipant("не json"), "Некорректный формат запроса");
        assertError(server.registerParticipant(null), "Некорректный формат запроса");
    }

    // ---------- регистрация эксперта ----------

    @Test
    public void testRegisterExpert() {
        ServerResponse response = registerExpert("petrov", "password1");
        assertEquals(200, response.getResponseCode());
        assertEquals("{}", response.getResponseData());
    }

    @Test
    public void testRegisterExpertEmptyDirections() {
        ServerResponse response = server.registerExpert(gson.toJson(
                new RegisterExpertDtoRequest("Пётр", "Петров", new ArrayList<>(), "petrov", "password1")));
        assertError(response, "Список направлений не должен быть пустым");
    }

    @Test
    public void testLoginIsUniqueAcrossParticipantsAndExperts() {
        registerParticipant("shared", "password1");
        assertError(registerExpert("shared", "password1"), "Этот логин уже используется");
    }

    // ---------- login / logout ----------

    @Test
    public void testLogin() {
        registerParticipant("ivanov", "password1");
        String token = loginAndGetToken("ivanov", "password1");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testLoginExpert() {
        registerExpert("petrov", "password1");
        assertNotNull(loginAndGetToken("petrov", "password1"));
    }

    @Test
    public void testLoginWrongPassword() {
        registerParticipant("ivanov", "password1");
        assertError(login("ivanov", "wrongpass1"), "Неверный логин или пароль");
    }

    @Test
    public void testLoginUnknownUser() {
        assertError(login("nobody", "password1"), "Неверный логин или пароль");
    }

    @Test
    public void testLogout() {
        registerParticipant("ivanov", "password1");
        String token = loginAndGetToken("ivanov", "password1");

        assertEquals(200, server.logout(token).getResponseCode());
        // токен больше недействителен
        assertError(server.logout(token), "Недействительный токен");
    }

    @Test
    public void testLogoutInvalidToken() {
        assertError(server.logout("unknown-token"), "Недействительный токен");
        assertError(server.logout(null), "Недействительный токен");
    }

    @Test
    public void testLoginAgainGivesNewToken() {
        registerParticipant("ivanov", "password1");
        String token1 = loginAndGetToken("ivanov", "password1");
        server.logout(token1);
        String token2 = loginAndGetToken("ivanov", "password1");

        assertNotEquals(token1, token2);
        assertError(server.logout(token1), "Недействительный токен");
        assertEquals(200, server.logout(token2).getResponseCode());
    }
}
