package football.model;

public enum PlayerStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String displayName;

    PlayerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PlayerStatus from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Player status is required.");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.equals("1")) {
            return ACTIVE;
        }
        if (normalized.equals("2")) {
            return INACTIVE;
        }
        for (PlayerStatus status : values()) {
            if (status.displayName.toLowerCase().equals(normalized)
                    || status.name().toLowerCase().equals(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status must be Active or Inactive.");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
