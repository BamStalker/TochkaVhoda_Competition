package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.service.ExpertService;
import academy.tochkavhoda.competition.service.ParticipantService;
import academy.tochkavhoda.competition.service.UserService;

public class Server {
    private final ParticipantService participantService = new ParticipantService();
    private final ExpertService expertService = new ExpertService();
    private final UserService userService = new UserService();

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
}
