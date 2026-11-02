package academy.tochkavhoda.competition.model;

import java.util.List;

public class Application {
    private int id;
    private String title;
    private String description;
    private List<String> directions;
    private int amount;
    private Participant participant; // автор заявки

    public Application(String title, String description, List<String> directions,
                       int amount, Participant participant) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
        this.participant = participant;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getDirections() { return directions; }
    public void setDirections(List<String> directions) { this.directions = directions; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }
}
