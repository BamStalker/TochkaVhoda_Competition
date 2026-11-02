package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

public interface UserDao {
    User getByLogin(String login);

    void login(int userId, String token) throws ServerException;

    void logout(String token) throws ServerException;

    User getUserByToken(String token) throws ServerException;
}
