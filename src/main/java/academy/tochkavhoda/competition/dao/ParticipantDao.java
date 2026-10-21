package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Participant;

public interface ParticipantDao {
    void insert(Participant participant) throws ServerException;
}
