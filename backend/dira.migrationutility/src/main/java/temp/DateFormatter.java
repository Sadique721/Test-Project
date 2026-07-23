package temp;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.time.Instant;

public class DateFormatter {
    public static void main(String[] args) {
    	
   String date=formatDateChange("13/11/19 00:30");
   System.out.println(date);
   
    }

    // Function to format the date into the required string format
    public static String formatDateChange( String inputDate) {
//    	08/02/19 00:30
        // Example input date as a string
      //  inputDate = "13/11/19 00:30";  // This can be replaced with dynamic data
        
        // Parse the input string into LocalDateTime using the specified format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(inputDate, inputFormatter);
        
        // Convert LocalDateTime to Instant (UTC)
        Instant instant = localDateTime.atZone(ZoneOffset.UTC).toInstant();
        
        // Format the date in the desired format
    	
        // Define the formatter for the desired format (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                                      .withZone(ZoneOffset.UTC);

        // Format the instant using the formatter
        String formattedDate = formatter.format(instant);
        
        // Return the result in the desired format
        return "\"addonEndDate\": \"" + formattedDate + "\"";
    }
}
