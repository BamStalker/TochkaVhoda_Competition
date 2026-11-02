package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class AddApplicationsDtoResponse {
    private List<Integer> applicationIds;

    public AddApplicationsDtoResponse(List<Integer> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public List<Integer> getApplicationIds() { return applicationIds; }
    public void setApplicationIds(List<Integer> applicationIds) { this.applicationIds = applicationIds; }
}
