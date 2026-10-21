package academy.tochkavhoda.competition.model;

public class Participant extends User {
    private String firmName;

    public Participant(String firstName, String lastName, String firmName, String login, String password) {
        super(firstName, lastName, login, password);
        this.firmName = firmName;
    }

    public String getFirmName() { return firmName; }
    public void setFirmName(String firmName) { this.firmName = firmName; }
}
