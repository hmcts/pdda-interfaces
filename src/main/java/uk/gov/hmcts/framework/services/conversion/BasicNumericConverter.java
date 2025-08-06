package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.services.CsServices;

import java.io.Serializable;



/**
 * Basic Numeric Parser implementation.

 * Other implementations could use this as a base to provide defaults.</p>

 */
public abstract class BasicNumericConverter implements ValueConverter, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Format an integer as a String. Returns String.valueOf(value).

     * @param value the integer to format.
     */
    @Override
    public String formatInt(int value) {
        return String.valueOf(value);
    }
    
    /**
     * Format a double as a String. Returns String.valueOf(value).

     * @param value the double to format.
     */
    @Override
    public String formatDouble(double value) {
        return String.valueOf(value);
    }
    
    /**
     * Format a float as a String. Returns String.valueOf(value).

     * @param value the float to format.
     */
    @Override
    public String formatFloat(float value) {
        return String.valueOf(value);
    }
    
    /**
     * Format a short as a String. Returns String.valueOf(value).

     * @param value the short to format.
     */
    @Override
    public String formatShort(short value) {
        return String.valueOf(value);
    }

    /**
     * Format a long as a String. Returns String.valueOf(value).

     * @param value the long to format.
     */
    @Override
    public String formatLong(long value) {
        return String.valueOf(value);
    }
    
    /**
     * Extract a short value from a String. Uses Short.parseShort.

     * @param value the String to parse.
     */
    @Override
    public short parseShort(String value) {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }
    
    /**
     * Extract a double value from a String. Uses Double.parseDouble.

     * @param value the String to parse.
     */
    @Override
    public double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }
   
    /**
     * Extract a float value from a String. Uses Float.parseFloat.

     * @param value the String to parse.
     */
    @Override
    public float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }
    
    /**
     * Extract an integer value from a String. Uses Integer.parseInt.

     * @param value the String to parse.
     */
    @Override
    public int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }
    
    /**
     * Extract a long value from a String. Uses Long.parseLong.

     * @param value the String to parse.
     * @throws ValueConvertException if the format is invalid.
     */
    @Override
    public long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }
}
