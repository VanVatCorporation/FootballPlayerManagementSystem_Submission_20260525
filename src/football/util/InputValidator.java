package football.util;

public final class InputValidator {
    private static final String RESERVED_DELIMITER = "|";

    private InputValidator() {
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty.");
        }
        String trimmed = value.trim();
        if (trimmed.contains(RESERVED_DELIMITER) || trimmed.contains("\n") || trimmed.contains("\r")) {
            throw new IllegalArgumentException(fieldName + " must not contain '|', line breaks, or invalid characters.");
        }
        return trimmed;
    }

    public static String normalizeId(String value, String fieldName) {
        String id = requireText(value, fieldName).toUpperCase();
        if (id.contains(" ")) {
            throw new IllegalArgumentException(fieldName + " must not contain spaces.");
        }
        return id;
    }

    public static void validateAge(int age) {
        if (age < 16 || age > 45) {
            throw new IllegalArgumentException("Player age must be between 16 and 45.");
        }
    }

    public static void validateShirtNumber(int shirtNumber) {
        if (shirtNumber < 1 || shirtNumber > 99) {
            throw new IllegalArgumentException("Shirt number must be between 1 and 99.");
        }
    }

    public static void validateBaseSalary(long baseSalary) {
        if (baseSalary <= 0) {
            throw new IllegalArgumentException("Base salary must be greater than zero.");
        }
    }

    public static void validatePerformanceNumbers(int goals, int assists, int yellowCards,
                                                  int redCards, int minutesPlayed) {
        if (goals < 0 || assists < 0 || yellowCards < 0 || redCards < 0 || minutesPlayed < 0) {
            throw new IllegalArgumentException("Performance values must not be negative.");
        }
        if (minutesPlayed > 120) {
            throw new IllegalArgumentException("Minutes played must be between 0 and 120.");
        }
        if (minutesPlayed == 0 && (goals != 0 || assists != 0 || yellowCards != 0 || redCards != 0)) {
            throw new IllegalArgumentException("If minutes played is 0, goals, assists, yellow cards, and red cards must all be 0.");
        }
    }
}
