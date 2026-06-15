package football.model;

public enum Position {
    GOALKEEPER("Goalkeeper"),
    DEFENDER("Defender"),
    MIDFIELDER("Midfielder"),
    FORWARD("Forward");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Position from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Position is required.");
        }
        String normalized = value.trim().replace(" ", "").toLowerCase();
        for (Position position : values()) {
            if (position.displayName.replace(" ", "").toLowerCase().equals(normalized)
                    || position.name().toLowerCase().equals(normalized)) {
                return position;
            }
        }
        throw new IllegalArgumentException("Position must be Goalkeeper, Defender, Midfielder, or Forward.");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
