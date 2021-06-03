package entity;

public enum TicketStatus {
    OPEN("OPEN"), WAITING("WAITING"), CLOSED("CLOSED"), LOCKED("LOCKED");

    private final String value;

    TicketStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
