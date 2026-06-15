package football.model;

public enum MatchType {
    FRIENDLY("Friendly"),
    LEAGUE("League"),
    CUP("Cup");

    private final String displayName;

    MatchType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MatchType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Match type is required.");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.equals("1")) {
            return FRIENDLY;
        }
        if (normalized.equals("2")) {
            return LEAGUE;
        }
        if (normalized.equals("3")) {
            return CUP;
        }
        for (MatchType type : values()) {
            if (type.displayName.toLowerCase().equals(normalized)
                    || type.name().toLowerCase().equals(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Match type must be Friendly, League, or Cup.");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
