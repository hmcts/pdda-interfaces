package uk.gov.hmcts.framework.services.conversion;

import java.util.Date;

/**
 * Basic String Converter.
 * 
 * <p>Provides methods for converting various common types to/from String.</p>
 * 
 */
public interface ValueConverter extends NumericValueConverter {

    /**
     * Extract a boolean value from a String.
     * 
     * @param value the String to parse.
     */
    boolean parseBoolean(String value);

    /**
     * Extract a byte value from a String.
     * 
     * @param value the String to parse.
     */
    byte parseByte(String value);

    /**
     * Extract a date value from a String.
     * 
     * @param value the String to parse.
     */
    Date parseDate(String value);

    /**
     * Format a boolean as a String.
     * 
     * @param value the boolean to format.
     */
    String formatBoolean(boolean value);

    /**
     * Format a byte as a String.
     * 
     * @param value the byte to format.
     */
    String formatByte(byte value);

    /**
     * Format a Date as a String.
     * 
     * @param value the Date to format.
     */
    String formatDate(Date value);
}
