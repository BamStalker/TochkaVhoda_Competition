package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ParticipantDao;
import academy.tochkavhoda.competition.daoimpl.ParticipantDaoImpl;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.EmptyDtoResponse;
import academy.tochkavhoda.competition.exception.ErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.server.ServerResponse;

public class ParticipantService {
    private final ParticipantDao participantDao = new ParticipantDaoImpl();

    public ServerResponse register(String requestJsonString) {
        try {
            RegisterParticipantDtoRequest request =
                    ServiceUtils.fromJson(requestJsonString, RegisterParticipantDtoRequest.class);
            validate(request);
            Participant participant = new Participant(request.getFirstName(), request.getLastName(),
                    request.getFirmName(), request.getLogin(), request.getPassword());
            participantDao.insert(participant);
            return ServiceUtils.ok(new EmptyDtoResponse());
        } catch (ServerException e) {
            return ServiceUtils.error(e);
        }
    }

    private static void validate(RegisterParticipantDtoRequest request) throws ServerException {
        ServiceUtils.validateNotBlank(request.getFirstName(), ErrorCode.EMPTY_FIRST_NAME);
        ServiceUtils.validateNotBlank(request.getLastName(), ErrorCode.EMPTY_LAST_NAME);
        ServiceUtils.validateNotBlank(request.getFirmName(), ErrorCode.EMPTY_FIRM_NAME);
        ServiceUtils.validateLogin(request.getLogin());
        ServiceUtils.validatePassword(request.getPassword());
    }
}
