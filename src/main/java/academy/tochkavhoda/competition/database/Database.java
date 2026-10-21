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

    private final Map<String, Participant> participants = new HashMap<>(); // login -> участник
    private final Map<String, Expert> experts = new HashMap<>();           // login -> эксперт
    private final Map<String, User> tokenToUser = new HashMap<>();         // token -> пользователь
    private final Map<String, String> loginToToken = new HashMap<>();      // login -> текущий token

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
        tokenToUser.clear();
        loginToToken.clear();
    }

    public void insertParticipant(Participant participant) throws ServerException {
        checkLoginFree(participant.getLogin());
        participants.put(participant.getLogin(), participant);
    }

    public void insertExpert(Expert expert) throws ServerException {
        checkLoginFree(expert.getLogin());
        experts.put(expert.getLogin(), expert);
    }

    /** Возвращает участника или эксперта с таким логином, либо null. */
    public User getUserByLogin(String login) {
        User user = participants.get(login);
        return user != null ? user : experts.get(login);
    }

    public void login(User user, String token) {
        String oldToken = loginToToken.remove(user.getLogin());
        if (oldToken != null) {
            tokenToUser.remove(oldToken);
        }
        loginToToken.put(user.getLogin(), token);
        tokenToUser.put(token, user);
    }

    public void logout(String token) throws ServerException {
        User user = token == null ? null : tokenToUser.remove(token);
        if (user == null) {
            throw new ServerException(ErrorCode.INVALID_TOKEN);
        }
        loginToToken.remove(user.getLogin());
    }

    public User getUserByToken(String token) throws ServerException {
        User user = token == null ? null : tokenToUser.get(token);
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
