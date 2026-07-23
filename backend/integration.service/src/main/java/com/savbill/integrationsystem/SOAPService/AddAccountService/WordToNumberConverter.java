package com.savbill.integrationsystem.SOAPService.AddAccountService;

import com.savbill.integrationsystem.exceptions.NumberParsingException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WordToNumberConverter {
//        implements CommandLineRunner {
    private static final List<String> ALLOWED_STRINGS = Arrays.asList(
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen", "twenty", "thirty", "forty", "fifty",
            "sixty", "seventy", "eighty", "ninety", "hundred", "thousand", "million",
            "billion", "trillion"
    );

    private static final Map<String, Long> WORD_TO_NUMBER = new HashMap<String,Long>() {{
        put("zero", 0L);            put("one", 1L);             put("two", 2L);            put("three", 3L);
        put("four", 4L);            put("five", 5L);            put("six", 6L);            put("seven", 7L);
        put("eight", 8L);           put("nine", 9L);            put("ten", 10L);           put("eleven", 11L);
        put("twelve", 12L);         put("thirteen", 13L);       put("fourteen", 14L);      put("fifteen", 15L);
        put("sixteen", 16L);        put("seventeen", 17L);      put("eighteen", 18L);      put("nineteen", 19L);
        put("twenty", 20L);         put("thirty", 30L);         put("forty", 40L);         put("fifty", 50L);
        put("sixty", 60L);          put("seventy", 70L);        put("eighty", 80L);        put("ninety", 90L);
    }};

    public static int convertWordToNumber(String input) {
        // Check for null or empty input
        if (input == null || input.isEmpty()) {
            throw new NumberParsingException("Input cannot be null or empty");
        }

        // Preprocess the input
        input = input.replaceAll("-", " ")
                .toLowerCase()
                .replaceAll(" and", " ")
                .trim();

        String[] splittedParts = input.split("\\s+");

        // Validate input
        validateInput(splittedParts);

        long finalResult = 0;
        long currentNumber = 0;

        for (String str : splittedParts) {
            long value = processWord(str, currentNumber);

            if (value == -1) {
                // Multiplier word
                switch (str.toLowerCase()) {
                    case "hundred":
                        if (currentNumber == 0) {
                            throw new NumberParsingException("Invalid use of 'hundred': no preceding number");
                        }
                        currentNumber *= 100;
                        break;
                    case "thousand":
                        if (currentNumber == 0) {
                            throw new NumberParsingException("Invalid use of 'thousand': no preceding number");
                        }
                        finalResult += currentNumber * 1000;
                        currentNumber = 0;
                        break;
                    case "million":
                        if (currentNumber == 0) {
                            throw new NumberParsingException("Invalid use of 'million': no preceding number");
                        }
                        finalResult += currentNumber * 1_000_000;
                        currentNumber = 0;
                        break;
                    case "billion":
                        if (currentNumber == 0) {
                            throw new NumberParsingException("Invalid use of 'billion': no preceding number");
                        }
                        finalResult += currentNumber * 1_000_000_000;
                        currentNumber = 0;
                        break;
                    case "trillion":
                        if (currentNumber == 0) {
                            throw new NumberParsingException("Invalid use of 'trillion': no preceding number");
                        }
                        finalResult += currentNumber * 1_000_000_000_000L;
                        currentNumber = 0;
                        break;
                }
            } else {
                currentNumber += value;
            }
        }

        finalResult += currentNumber;

        // Validate final result fits in int range
        if (finalResult > Integer.MAX_VALUE) {
            throw new NumberParsingException("Number exceeds maximum integer value: " + finalResult);
        }

        return (int) finalResult;
    }

    private static void validateInput(String[] words) {
        for (String str : words) {
            if (!ALLOWED_STRINGS.contains(str.toLowerCase())) {
                throw new NumberParsingException("Invalid word found: " + str);
            }
        }
    }

    private static long processWord(String word, long currentNumber) {
        word = word.toLowerCase();

        // Check if it's a basic number word
        Long numberValue = WORD_TO_NUMBER.get(word);
        if (numberValue != null) {
            return numberValue;
        }

        // If not a basic number, it's a multiplier
        return -1;
    }


    public static String convertNumberToWord(long number) {
        if (number == 0) {
            return "zero";
        }

        StringBuilder result = new StringBuilder();

        // Define the chunks (1,000,000,000, etc)
        String[] units = {"", "thousand", "million", "billion", "trillion"};
        int unitIndex = 0;

        while (number > 0) {
            if (number % 1000 != 0) {
                String part = convertThreeDigitChunkToWord((int) (number % 1000));
                if (unitIndex > 0) {
                    part += " " + units[unitIndex];
                }
                if (result.length() > 0) {
                    result.insert(0, " " + part+" ");
                } else {
                    result.insert(0, part+" ");
                }
            }
            number /= 1000;
            unitIndex++;
        }

        return result.toString().trim();
    }

    private static String convertThreeDigitChunkToWord(int number) {
        if (number == 0) {
            return "";
        }

        String[] lessThan20 = {"", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
                "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
                "eighteen", "nineteen"};

        String[] tens = {"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

        StringBuilder sb = new StringBuilder();

        if (number >= 100) {
            sb.append(lessThan20[number / 100]).append(" hundred");
            number %= 100;
            if (number > 0) {
                sb.append(" and ");
            }
        }

        if (number >= 20) {
            sb.append(tens[number / 10]);
            if (number % 10 != 0) {
                sb.append("-");
            }
            sb.append(lessThan20[number % 10]);
        } else if (number > 0) {
            sb.append(lessThan20[number]);
        }

        return sb.toString().trim();
    }

//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("enter number to convert");
//        Scanner sc = new Scanner(System.in);
//        while (true){
//            long l = sc.nextLong();
//            String s = convertNumberToWord(l);
//            System.out.println(l +" converted to "+ s);
//        }
//    }
}
