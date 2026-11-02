package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;

public interface ApplicationDao {
    /** Сохраняет заявку и возвращает присвоенный ей id. */
    int insert(Application application);

    Application getById(int applicationId) throws ServerException;

    void delete(int applicationId) throws ServerException;
}
