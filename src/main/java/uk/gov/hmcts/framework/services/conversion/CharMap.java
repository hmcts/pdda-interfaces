package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CharMap extends BooleanMap {

    public CharMap(ValueConverter converter) {
        super(converter);
    }
    
    /**
     * Set a char value. The value is formatted as a String of length 1.
     */
    public void putChar(String name, char value) {
        getMap().put(name, String.valueOf(value));
    }

    /**
     * Get a char value. Returns \0 if there is no entry for the name, or it is null. Otherwise, if
     * the length is exactly 1, return the only char. Otherwise throw a parse exception.
     */
    public char getChar(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            if (ONE.equals(stringValue.length())) {
                return stringValue.charAt(0);
            }
            throw new ValueConvertException(name, Character.TYPE, stringValue);
        }
        return '\0';
    }
    
    private boolean isTypeChar(Class<?> type) {
        return type == Character.TYPE || type == Character.class;
    }

    private void copyCharToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Character charValue = (Character) method.invoke(bean, args);
        if (charValue != null) {
            stringValue = charValue.toString();
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeChar(type)) {
                copyCharToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, ByteMap.class);
            throw ex;
        }
    }
}
