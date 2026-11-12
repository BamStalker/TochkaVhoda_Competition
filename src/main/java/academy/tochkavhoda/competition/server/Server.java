package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.service.ExpertService;
import academy.tochkavhoda.competition.service.ParticipantService;
import academy.tochkavhoda.competition.service.UserService;

public class Server {
    private final ParticipantService participantService = new ParticipantService();
    private final ExpertService expertService = new ExpertService();
    private final UserService userService = new UserService();
    private final ApplicationApi applicationApi = new ApplicationApi();
    private final ExpertApi expertApi = new ExpertApi();

    /** Создание сервера начинает работу с пустой "базой данных". */
    public Server() {
        Database.getInstance().clear();
    }

    public ServerResponse registerParticipant(String requestJsonString) {
        return participantService.register(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return expertService.register(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    public ServerResponse addApplications(String token, String requestJsonString) {
        return applicationApi.addApplications(token, requestJsonString);
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return applicationApi.cancelApplication(token, requestJsonString);
    }

    public ServerResponse rateApplication(String token, String requestJsonString) {
        return expertApi.rateApplication(token, requestJsonString);
    }

    public ServerResponse removeRating(String token, String requestJsonString) {
        return expertApi.removeRating(token, requestJsonString);
    }

    public ServerResponse getApplications(String token, String requestJsonString) {
        return expertApi.getApplications(token, requestJsonString);
    }

    public ServerResponse getRatedApplications(String token) {
        return expertApi.getRatedApplications(token);
    }
}
