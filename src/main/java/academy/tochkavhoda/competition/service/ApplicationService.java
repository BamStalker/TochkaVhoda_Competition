package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;

import java.util.ArrayList;
import java.util.List;

/** Бизнес-логика заявок. Данные приходят уже проверенными и разобранными слоем Server. */
public class ApplicationService {
    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();

    /** Создаёт заявку участника и возвращает её id. */
    public ResultService<Integer> addApplication(int participantId, String title, String description,
                                                 List<String> directions, int amount) {
        try {
            Participant participant = getParticipant(participantId);
            Application application = new Application(title, description,
                    new ArrayList<>(directions), amount, participant);
            return ResultService.success(applicationDao.insert(application));
        } catch (ServerException e) {
            return ResultService.failure(e);
        }
    }

    /** Отменяет (удаляет) заявку. Отменить можно только свою заявку. */
    public ResultService<Void> cancelApplication(int participantId, int applicationId) {
        try {
            Application application = applicationDao.getById(applicationId);
            if (application.getParticipant().getId() != participantId) {
                throw new ServerException(ErrorCode.NOT_APPLICATION_OWNER);
            }
            applicationDao.delete(applicationId);
            return new ResultService<>(true);
        } catch (ServerException e) {
            return new ResultService<>(false, e);
        }
    }

    private Participant getParticipant(int participantId) throws ServerException {
        User user = userDao.getById(participantId);
        if (!(user instanceof Participant)) {
            throw new ServerException(ErrorCode.NOT_PARTICIPANT);
        }
        return (Participant) user;
    }
}
