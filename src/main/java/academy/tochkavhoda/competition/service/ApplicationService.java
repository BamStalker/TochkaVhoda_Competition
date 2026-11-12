package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.RatingDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Бизнес-логика заявок. Данные приходят уже проверенными и разобранными слоем Server. */
public class ApplicationService {
    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();
    private final RatingDao ratingDao = new RatingDaoImpl();

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

    /** Отменяет (удаляет) заявку вместе со всеми её оценками. Отменить можно только свою заявку. */
    public ResultService<Void> cancelApplication(int participantId, int applicationId) {
        try {
            Application application = applicationDao.getById(applicationId);
            if (application.getParticipant().getId() != participantId) {
                throw new ServerException(ErrorCode.NOT_APPLICATION_OWNER);
            }
            ratingDao.deleteByApplication(applicationId);
            applicationDao.delete(applicationId);
            return new ResultService<>(true);
        } catch (ServerException e) {
            return new ResultService<>(false, e);
        }
    }

    /**
     * Заявки по направлениям эксперта. Если directions пустой, возвращаются заявки
     * по всем направлениям эксперта, иначе только по указанным (все они должны быть из его направлений).
     */
    public ResultService<List<Application>> getApplicationsForExpert(int expertId, List<String> directions) {
        try {
            Expert expert = getExpert(expertId);
            Set<String> expertDirections = DirectionUtils.normalize(expert.getDirections());
            Set<String> wanted = expertDirections;
            if (!directions.isEmpty()) {
                wanted = DirectionUtils.normalize(directions);
                if (!expertDirections.containsAll(wanted)) {
                    throw new ServerException(ErrorCode.DIRECTION_NOT_ALLOWED);
                }
            }
            List<Application> result = new ArrayList<>();
            for (Application application : applicationDao.getAll()) {
                if (DirectionUtils.intersects(application.getDirections(), wanted)) {
                    result.add(application);
                }
            }
            return ResultService.success(result);
        } catch (ServerException e) {
            return ResultService.failure(e);
        }
    }

    private Participant getParticipant(int participantId) throws ServerException {
        User user = userDao.getById(participantId);
        if (!(user instanceof Participant)) {
            throw new ServerException(ErrorCode.NOT_PARTICIPANT);
        }
        return (Participant) user;
    }

    private Expert getExpert(int expertId) throws ServerException {
        User user = userDao.getById(expertId);
        if (!(user instanceof Expert)) {
            throw new ServerException(ErrorCode.NOT_EXPERT);
        }
        return (Expert) user;
    }
}
