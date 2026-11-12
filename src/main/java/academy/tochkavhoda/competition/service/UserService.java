package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDaoImpl();

    public ServerResponse login(String requestJsonString) {
        try {
            LoginDtoRequest request = ServiceUtils.fromJson(requestJsonString, LoginDtoRequest.class);
            validate(request);
            User user = userDao.getByLogin(request.getLogin());
            if (user == null || !user.getPassword().equals(request.getPassword())) {
                throw new ServerException(ErrorCode.INVALID_LOGIN_OR_PASSWORD);
            }
            String token = UUID.randomUUID().toString();
            userDao.login(user.getId(), token);
            return ServiceUtils.ok(new LoginDtoResponse(token));
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    public ServerResponse logout(String token) {
        try {
            userDao.logout(token);
            return ServiceUtils.ok(new EmptyDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    /**
     * Находит по токену пользователя и проверяет, что это участник.
     * Возвращает id участника. Вызывается слоем Server до обращения к бизнес-логике.
     */
    public ResultService<Integer> getParticipantId(String token) {
        try {
            User user = userDao.getUserByToken(token);
            if (!(user instanceof Participant)) {
                throw new ServerException(ErrorCode.NOT_PARTICIPANT);
            }
            return ResultService.success(user.getId());
        } catch (ServerException e) {
            return ResultService.failure(e);
        }
    }

    private static void validate(LoginDtoRequest request) throws ServerException {
        ServiceUtils.validateNotBlank(request.getLogin(), ErrorCode.EMPTY_LOGIN);
        ServiceUtils.validateNotBlank(request.getPassword(), ErrorCode.EMPTY_PASSWORD);
    }
}
