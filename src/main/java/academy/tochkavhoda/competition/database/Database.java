package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.Rating;
import academy.tochkavhoda.competition.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static Database instance;

    private final Map<Integer, User> usersById = new HashMap<>();
    private int nextUserId = 1;
    private final Map<String, Participant> participants = new HashMap<>(); // login -> участник
    private final Map<String, Expert> experts = new HashMap<>();           // login -> эксперт
    private final Map<String, User> sessionsUser = new HashMap<>();        // token -> пользователь
    private final Map<Integer, Application> applications = new HashMap<>(); // id -> заявка
    private int nextApplicationId = 1;
    private final Map<Integer, Map<Integer, Rating>> ratings = new HashMap<>(); // id заявки -> (id эксперта -> оценка)

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
        applications.clear();
        ratings.clear();
        nextUserId = 1;
        nextApplicationId = 1;
    }

    // ---------- пользователи ----------

    public void insertParticipant(Participant participant) throws ServerException {
        checkLoginFree(participant.getLogin());
        participant.setId(nextUserId++);
        participants.put(participant.getLogin(), participant);
        usersById.put(participant.getId(), participant);
    }

    public void insertExpert(Expert expert) throws ServerException {
        checkLoginFree(expert.getLogin());
        expert.setId(nextUserId++);
        experts.put(expert.getLogin(), expert);
        usersById.put(expert.getId(), expert);
    }

    /** Возвращает участника или эксперта с таким логином, либо null. */
    public User getUserByLogin(String login) {
        User user = participants.get(login);
        return user != null ? user : experts.get(login);
    }

    public User getUserById(int userId) throws ServerException {
        User user = usersById.get(userId);
        if (user == null) {
            throw new ServerException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    // ---------- сессии ----------

    public void addSession(String token, User user) {
        // удаляем прежние сессии этого пользователя (если он входил и не выходил)
        sessionsUser.values().removeIf(u -> u.getId() == user.getId());
        sessionsUser.put(token, user);
    }

    public void addSession(String token, int userId) throws ServerException {
        User user = getUserById(userId);
        addSession(token, user);
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

    // ---------- заявки ----------

    /** Сохраняет заявку, присваивает ей id и возвращает его. */
    public int insertApplication(Application application) {
        application.setId(nextApplicationId++);
        applications.put(application.getId(), application);
        return application.getId();
    }

    public Application getApplicationById(int applicationId) throws ServerException {
        Application application = applications.get(applicationId);
        if (application == null) {
            throw new ServerException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        return application;
    }

    public void deleteApplication(int applicationId) throws ServerException {
        if (applications.remove(applicationId) == null) {
            throw new ServerException(ErrorCode.APPLICATION_NOT_FOUND);
        }
    }

    /** Все заявки, упорядоченные по id. */
    public List<Application> getAllApplications() {
        List<Application> result = new ArrayList<>(applications.values());
        result.sort(Comparator.comparingInt(Application::getId));
        return result;
    }

    // ---------- оценки ----------

    /** Сохраняет оценку. Если этот эксперт уже оценивал заявку, старая оценка заменяется. */
    public void saveRating(Rating rating) {
        ratings.computeIfAbsent(rating.getApplication().getId(), k -> new HashMap<>())
                .put(rating.getExpert().getId(), rating);
    }

    public void deleteRating(int applicationId, int expertId) throws ServerException {
        Map<Integer, Rating> byExpert = ratings.get(applicationId);
        if (byExpert == null || byExpert.remove(expertId) == null) {
            throw new ServerException(ErrorCode.RATING_NOT_FOUND);
        }
    }

    public void deleteRatingsByApplication(int applicationId) {
        ratings.remove(applicationId);
    }

    /** Оценки эксперта, упорядоченные по id заявки. */
    public List<Rating> getRatingsByExpert(int expertId) {
        List<Rating> result = new ArrayList<>();
        for (Map<Integer, Rating> byExpert : ratings.values()) {
            Rating rating = byExpert.get(expertId);
            if (rating != null) {
                result.add(rating);
            }
        }
        result.sort(Comparator.comparingInt(r -> r.getApplication().getId()));
        return result;
    }

    private void checkLoginFree(String login) throws ServerException {
        if (participants.containsKey(login) || experts.containsKey(login)) {
            throw new ServerException(ErrorCode.LOGIN_ALREADY_USED);
        }
    }
}
