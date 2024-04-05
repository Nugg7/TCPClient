package com.example.clienttcp;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;

public class DecimalTextFormatter extends TextFormatter<Number> {
    private static DecimalFormat format = new DecimalFormat("#.0;-#.0"); // format variable

    public DecimalTextFormatter(int minDecimals, int maxDecimals) {
        super(getStringConverter(minDecimals, maxDecimals), 0, getUnaryOperator(maxDecimals, false, -1));
    }

    private static StringConverter<Number> getStringConverter(int minDecimals, int maxDecimals) {
        // Returns a StringConverter that formats and parses numbers according to the format specified with variable format (first lines of class)
        return new StringConverter<>() {
            @Override
            // Formats the number into the format specified with variable format (first lines of class)
            public String toString(Number value) {
                if (value == null) {
                    return "";
                }
                return format.format(value);
            }

            @Override
            // Parses the String into a number
            public Number fromString(String text) {
                ParsePosition parsePosition = new ParsePosition(0);
                Number parsed = format.parse(text, parsePosition);
                if (parsePosition.getIndex() != text.length()) {
                    return null;
                }
                return parsed;
            }
        };
    }

    private static UnaryOperator<TextFormatter.Change> getUnaryOperator(int maxDecimals, boolean allowNegative, int maxLength) {
        //Filters the modified text input and makes sure that:
        // 1. Only digits, decimal point, and negative sign are allowed (if allowNegative is false it doesn't allow the negative sign)
        // 2. There's a limit for the number of decimal places
        // 3. There's a limit for the total length
        return change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }

            // Allow only digits, decimal point, and negative sign
            if (!newText.matches("-?\\d*[.,]?\\d*")) {
                return null;
            }

            // Limit the number of decimal places
            if (maxDecimals >= 0) {
                newText = newText.replace(",", ".");
                int decimalIndex = newText.indexOf(".");
                if (decimalIndex >= 0 && newText.substring(decimalIndex + 1).length() > maxDecimals) {
                    return null;
                }
            }

            // Limit total length
            if (maxLength >= 0 && newText.length() > maxLength) {
                return null;
            }

            // Allow negative sign if specified
            if (!allowNegative && newText.startsWith("-")) {
                return null;
            }

            return change;
        };
    }
}
