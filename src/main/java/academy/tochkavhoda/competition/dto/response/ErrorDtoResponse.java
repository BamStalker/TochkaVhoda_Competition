package academy.tochkavhoda.competition.dto.response;

public class ErrorDtoResponse {
    private String error;

    public ErrorDtoResponse(String error) {
        this.error = error;
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
