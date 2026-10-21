package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

public class UserDaoImpl implements UserDao {
    private final Database database = Database.getInstance();

    @Override
    public User getByLogin(String login) {
        return database.getUserByLogin(login);
    }

    @Override
    public void login(User user, String token) {
        database.login(user, token);
    }

    @Override
    public void logout(String token) throws ServerException {
        database.logout(token);
    }

    @Override
    public User getUserByToken(String token) throws ServerException {
        return database.getUserByToken(token);
    }
}
