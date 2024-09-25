package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.services.CsServices;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Basic Parser implementation.
 * 
 * <p>Other implementations could use this as a base to provide defaults.</p>
 * 
 */
public class BasicConverter extends BasicNumericConverter implements ValueConverter, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DDMMYYYY = "dd/MM/yyyy";
    private static final String FALSE_STRING = "false";
    private static final String TRUE_STRING = "true";

    protected final DateFormat dateFormat;

    public BasicConverter(DateFormat dateFormat) {
        super();
        this.dateFormat = dateFormat;
        this.dateFormat.setLenient(false);
    }

    /**
     * Default constructor uses date format: dd/MM/yyyy.
     */
    public BasicConverter() {
        this(new SimpleDateFormat(DDMMYYYY, Locale.getDefault()));
    }

    private DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Extract a boolean value from a String. Accepts "true" or "false".
     * 
     * @param value the String to parse.
     */
    @Override
    public boolean parseBoolean(String value) {
        if (TRUE_STRING.equals(value)) {
            return true;
        }
        if (FALSE_STRING.equals(value)) {
            return false;
        }
        throw new ValueConvertException(value);
    }

    /**
     * Extract a byte value from a String. Uses Byte.parseByte.
     * 
     * @param value the String to parse.
     */
    @Override
    public byte parseByte(String value) {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }

    /**
     * Extract a date value from a String. Uses a SimpleDataFormat("dd/MM/yyyy")
     * 
     * @param value the String to parse.
     */
    @Override
    public Date parseDate(String value) {
        try {
            return getDateFormat().parse(value);
        } catch (ParseException e) {
            ValueConvertException ex = new ValueConvertException(value, e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicConverter.class);
            throw ex;
        }
    }

    /**
     * Format a boolean as a String. Returns "true" or "false".
     * 
     * @param value the boolean to format.
     */
    @Override
    public String formatBoolean(boolean value) {
        return value ? TRUE_STRING : FALSE_STRING;
    }

    /**
     * Format a byte as a String. Returns String.valueOf(value).
     * 
     * @param value the byte to format.
     */
    @Override
    public String formatByte(byte value) {
        return String.valueOf(value);
    }

    /**
     * Format a Date as a String. Uses a SimpleDataFormat("dd/MM/yyyy")
     * 
     * @param value the Date to format.
     */
    @Override
    public String formatDate(Date value) {
        return getDateFormat().format(value);
    }
}
