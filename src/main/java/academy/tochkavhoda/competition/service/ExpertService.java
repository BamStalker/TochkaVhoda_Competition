package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ExpertDao;
import academy.tochkavhoda.competition.daoimpl.ExpertDaoImpl;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;

public class ExpertService {
    private final ExpertDao expertDao = new ExpertDaoImpl();

    public ServerResponse register(String requestJsonString) {
        try {
            RegisterExpertDtoRequest request =
                    ServiceUtils.fromJson(requestJsonString, RegisterExpertDtoRequest.class);
            validate(request);
            Expert expert = new Expert(request.getFirstName(), request.getLastName(),
                    new ArrayList<>(request.getDirections()), request.getLogin(), request.getPassword());
            expertDao.insert(expert);
            return ServiceUtils.ok(new EmptyDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    private static void validate(RegisterExpertDtoRequest request) throws ServerException {
        ServiceUtils.validateNotBlank(request.getFirstName(), ErrorCode.EMPTY_FIRST_NAME);
        ServiceUtils.validateNotBlank(request.getLastName(), ErrorCode.EMPTY_LAST_NAME);
        if (request.getDirections() == null || request.getDirections().isEmpty()
                || request.getDirections().stream().anyMatch(ServiceUtils::isBlank)) {
            throw new ServerException(ErrorCode.EMPTY_DIRECTIONS);
        }
        ServiceUtils.validateLogin(request.getLogin());
        ServiceUtils.validatePassword(request.getPassword());
    }
}
