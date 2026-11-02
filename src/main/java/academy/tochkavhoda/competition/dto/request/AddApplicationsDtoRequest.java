package academy.tochkavhoda.competition.dto.request;

import java.util.List;

public class AddApplicationsDtoRequest {
    private List<ApplicationDtoRequest> applications;

    public AddApplicationsDtoRequest(List<ApplicationDtoRequest> applications) {
        this.applications = applications;
    }

    public List<ApplicationDtoRequest> getApplications() { return applications; }
    public void setApplications(List<ApplicationDtoRequest> applications) { this.applications = applications; }
}
