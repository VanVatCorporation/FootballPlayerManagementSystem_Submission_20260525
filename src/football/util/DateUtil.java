package football.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class DateUtil {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DateUtil() {
    }

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), FORMATTER);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Date must be a valid calendar date in dd/MM/yyyy format.");
        }
    }

    public static String formatDate(LocalDate date) {
        return FORMATTER.format(date);
    }

    public static void validateMonthYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2000 and 2100.");
        }
    }

    public static boolean matchesMonthYear(LocalDate date, int month, int year) {
        return date.getMonthValue() == month && date.getYear() == year;
    }
}
