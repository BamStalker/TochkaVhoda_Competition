package academy.tochkavhoda.competition.dto.request;

import java.util.List;

/** Описание одной заявки внутри запроса на добавление. */
public class ApplicationDtoRequest {
    private String title;
    private String description;
    private List<String> directions;
    private int amount;

    public ApplicationDtoRequest(String title, String description, List<String> directions, int amount) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getDirections() { return directions; }
    public void setDirections(List<String> directions) { this.directions = directions; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
