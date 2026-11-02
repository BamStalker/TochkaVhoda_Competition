package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.AddApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.CancelApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationsDtoResponse;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.List;

public class ApplicationService {
    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();

    /** Добавляет одну или несколько заявок. Если хотя бы одна некорректна, не добавляется ни одна. */
    public ServerResponse addApplications(String token, String requestJsonString) {
        try {
            Participant participant = getParticipantByToken(token);
            AddApplicationsDtoRequest request =
                    ServiceUtils.fromJson(requestJsonString, AddApplicationsDtoRequest.class);
            validate(request);

            List<Integer> ids = new ArrayList<>();
            for (ApplicationDtoRequest item : request.getApplications()) {
                Application application = new Application(item.getTitle(), item.getDescription(),
                        new ArrayList<>(item.getDirections()), item.getAmount(), participant);
                ids.add(applicationDao.insert(application));
            }
            return ServiceUtils.ok(new AddApplicationsDtoResponse(ids));
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    /** Отменяет (удаляет) заявку. Отменить можно только свою заявку. */
    public ServerResponse cancelApplication(String token, String requestJsonString) {
        try {
            Participant participant = getParticipantByToken(token);
            CancelApplicationDtoRequest request =
                    ServiceUtils.fromJson(requestJsonString, CancelApplicationDtoRequest.class);
            Application application = applicationDao.getById(request.getApplicationId());
            if (application.getParticipant().getId() != participant.getId()) {
                throw new ServerException(ErrorCode.NOT_APPLICATION_OWNER);
            }
            applicationDao.delete(application.getId());
            return ServiceUtils.ok(new EmptyDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    private Participant getParticipantByToken(String token) throws ServerException {
        User user = userDao.getUserByToken(token);
        if (!(user instanceof Participant)) {
            throw new ServerException(ErrorCode.NOT_PARTICIPANT);
        }
        return (Participant) user;
    }

    private static void validate(AddApplicationsDtoRequest request) throws ServerException {
        List<ApplicationDtoRequest> items = request.getApplications();
        if (items == null || items.isEmpty()) {
            throw new ServerException(ErrorCode.EMPTY_APPLICATIONS);
        }
        for (ApplicationDtoRequest item : items) {
            if (item == null) {
                throw new ServerException(ErrorCode.INVALID_JSON);
            }
            ServiceUtils.validateNotBlank(item.getTitle(), ErrorCode.EMPTY_TITLE);
            ServiceUtils.validateNotBlank(item.getDescription(), ErrorCode.EMPTY_DESCRIPTION);
            if (item.getDirections() == null || item.getDirections().isEmpty()
                    || item.getDirections().stream().anyMatch(ServiceUtils::isBlank)) {
                throw new ServerException(ErrorCode.EMPTY_DIRECTIONS);
            }
            if (item.getAmount() <= 0) {
                throw new ServerException(ErrorCode.INVALID_AMOUNT);
            }
        }
    }
}
