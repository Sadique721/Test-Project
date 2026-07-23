package utility;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {

    private static final DateTimeFormatter FORMATTER_WITH_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATTER_DATE_ONLY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatToStandard(String inputDate) {
        // Handle null or blank input
        if (inputDate == null || inputDate.trim().isEmpty()) {
            return OUTPUT_FORMATTER.format(LocalDateTime.now());
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(inputDate, FORMATTER_WITH_TIME);
            return OUTPUT_FORMATTER.format(dateTime);
        } catch (DateTimeParseException e1) {
            try {
                LocalDate date = LocalDate.parse(inputDate, FORMATTER_DATE_ONLY);
                return OUTPUT_FORMATTER.format(date.atStartOfDay());
            } catch (DateTimeParseException e2) {
                System.err.println("Invalid format. Using current date and time.");
                return OUTPUT_FORMATTER.format(LocalDateTime.now());
            }
        }
    }
}
