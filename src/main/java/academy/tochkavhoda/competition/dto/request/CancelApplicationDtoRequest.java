package academy.tochkavhoda.competition.dto.request;

public class CancelApplicationDtoRequest {
    private int applicationId;

    public CancelApplicationDtoRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
}
