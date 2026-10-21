package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ExpertDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;

public class ExpertDaoImpl implements ExpertDao {
    private final Database database = Database.getInstance();

    @Override
    public void insert(Expert expert) throws ServerException {
        database.insertExpert(expert);
    }
}
