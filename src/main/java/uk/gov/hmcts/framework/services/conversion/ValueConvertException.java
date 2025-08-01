package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsBusinessException;

import java.io.Serializable;

public class ValueConvertException extends CsBusinessException implements Serializable {
    private static final long serialVersionUID = 1L;

    private String propertyName;

    private Class<?> propertyType;

    private String propertyValue;

    private static final String VALUE_CONVERSION_ERROR_KEY = "framework.services.conversion.error";

    public ValueConvertException(String propertyValue) {
        super();
        setPropertyName(null);
        setPropertyType(null);
        setPropertyValue(propertyValue);
    }

    /**
     * ValueConvertException.

     * @param propertyValue String
     * @param exception the initial cause
     */
    public ValueConvertException(String propertyValue, Throwable exception) {
        super(VALUE_CONVERSION_ERROR_KEY, "propValue=" + propertyValue, exception);
        setPropertyName(null);
        setPropertyType(null);
        setPropertyValue(propertyValue);
    }

    public ValueConvertException(String propertyName, Class<?> propertyType, String propertyValue,
        Throwable exception) {
        // following pattern established in ValueConvertException(String
        // propertyName,
        // Class propertyType,
        // String propertyValue)
        super(VALUE_CONVERSION_ERROR_KEY,
            "propValue=" + propertyValue + "propertyName=" + propertyName, exception);
        setPropertyName(propertyName);
        setPropertyType(propertyType);
        setPropertyValue(propertyValue);
    }

    /**
     * An ConvertException has a name, type and value.

     * @param propertyName identifies the field whose parse failed.
     * @param propertyType is the type required, e.g. java.util.Date.class, or Double.TYPE.
     * @param propertyValue is the actual value that failed to parse.
     */
    public ValueConvertException(String propertyName, Class<?> propertyType, String propertyValue) {

        super(VALUE_CONVERSION_ERROR_KEY,
            "propertyValue=" + propertyValue + "propertyName=" + propertyName);
        setPropertyName(propertyName);
        setPropertyType(propertyType);
        setPropertyValue(propertyValue);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
    
    private void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    private void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }
    
    private void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
