package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;

import java.util.List;

public interface ApplicationDao {
    /** Сохраняет заявку и возвращает присвоенный ей id. */
    int insert(Application application);

    Application getById(int applicationId) throws ServerException;

    List<Application> getAll();

    void delete(int applicationId) throws ServerException;
}
