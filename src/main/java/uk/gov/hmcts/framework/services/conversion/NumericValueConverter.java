package uk.gov.hmcts.framework.services.conversion;

public interface NumericValueConverter {

    /**
     * Extract a short value from a String.
     * 
     * @param value the String to parse.
     */
    short parseShort(String value);

    /**
     * Extract an integer value from a String.
     * 
     * @param value the String to parse.
     */
    int parseInt(String value);

    /**
     * Extract a long value from a String.
     * 
     * @param value the String to parse.
     */
    long parseLong(String value);

    /**
     * Extract a float value from a String.
     * 
     * @param value the String to parse.
     */
    float parseFloat(String value);

    /**
     * Extract a double value from a String.
     * 
     * @param value the String to parse.
     */
    double parseDouble(String value);

    /**
     * Format a short as a String.
     * 
     * @param value the short to format.
     */
    String formatShort(short value);

    /**
     * Format an integer as a String.
     * 
     * @param value the integer to format.
     */
    String formatInt(int value);

    /**
     * Format a long as a String.
     * 
     * @param value the long to format.
     */
    String formatLong(long value);

    /**
     * Format a float as a String.
     * 
     * @param value the float to format.
     */
    String formatFloat(float value);

    /**
     * Format a double as a String.
     * 
     * @param value the double to format.
     */
    String formatDouble(double value);
}
