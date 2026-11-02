package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Database instance;

    private final Map<Integer, User> usersById = new HashMap<>();
    private int nextUserId = 1;
    private final Map<String, Participant> participants = new HashMap<>(); // login -> участник
    private final Map<String, Expert> experts = new HashMap<>();           // login -> эксперт
    private final Map<String, User> sessionsUser = new HashMap<>();         // token -> пользователь

    private Database() {
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void clear() {
        participants.clear();
        experts.clear();
        sessionsUser.clear();
        usersById.clear();
        nextUserId = 1;
    }

    public void insertParticipant(Participant participant) throws ServerException {
        checkLoginFree(participant.getLogin());
        participant.setId(nextUserId++);
        participants.put(participant.getLogin(), participant);
        usersById.put(participant.getId(), participant);
    }

    public void insertExpert(Expert expert) throws ServerException {
        expert.setId(nextUserId++);
        experts.put(expert.getLogin(), expert);
        usersById.put(expert.getId(), expert);
    }

    /** Возвращает участника или эксперта с таким логином, либо null. */
    public User getUserByLogin(String login) {
        User user = participants.get(login);
        return user != null ? user : experts.get(login);
    }

    // ДЗ: написать метод getUserById здесь, потом добавить его в addSession через id,
    // чтобы оттуда передать его в метод через user

    public User getUserById(int userId) throws ServerException {
        User user = usersById.get(userId);
        if (user == null) {
            throw new ServerException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public void addSession(String token, User user) {
        // удаляем прежние сессии этого пользователя (если он входил и не выходил)
        sessionsUser.values().removeIf(u -> u.getId() == user.getId());
        sessionsUser.put(token, user);
    }

    public void addSession(String token, int userId) throws ServerException {
        User user = getUserById(userId);   // находим пользователя по id
        addSession(token, user);           // и передаём его в метод выше
    }

    public void logout(String token) throws ServerException {
        User user = token == null ? null : sessionsUser.remove(token);
        if (user == null) {
            throw new ServerException(ErrorCode.INVALID_TOKEN);
        }
    }

    public User getUserByToken(String token) throws ServerException {
        User user = token == null ? null : sessionsUser.get(token);
        if (user == null) {
            throw new ServerException(ErrorCode.INVALID_TOKEN);
        }
        return user;
    }

    private void checkLoginFree(String login) throws ServerException {
        if (participants.containsKey(login) || experts.containsKey(login)) {
            throw new ServerException(ErrorCode.LOGIN_ALREADY_USED);
        }
    }
}
