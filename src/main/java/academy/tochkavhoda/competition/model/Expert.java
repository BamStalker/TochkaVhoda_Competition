package academy.tochkavhoda.competition.model;

import java.util.List;

public class Expert extends User {
    private List<String> directions;

    public Expert(String firstName, String lastName, List<String> directions, String login, String password) {
        super(firstName, lastName, login, password);
        this.directions = directions;
    }

    public List<String> getDirections() { return directions; }
    public void setDirections(List<String> directions) { this.directions = directions; }
}
