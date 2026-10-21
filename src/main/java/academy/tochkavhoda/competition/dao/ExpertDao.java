package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;

public interface ExpertDao {
    void insert(Expert expert) throws ServerException;
}
