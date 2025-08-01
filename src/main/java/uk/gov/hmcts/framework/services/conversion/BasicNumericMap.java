package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BasicNumericMap extends ExtraNumericMap {

    public BasicNumericMap(ValueConverter converter) {
        super(converter);
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeShort(type)) {
                copyShortToMap(propertyName, method, bean);
            } else if (isTypeInteger(type)) {
                copyIntegerToMap(propertyName, method, bean);
            } else if (isTypeLong(type)) {
                copyLongToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicNumericMap.class);
            throw ex;
        }
    }
    
    // Integer
    
    /**
     * Set an int value. The int value is formatted using the supplied getConverter().
     */
    public void putInt(String name, int value) {
        getMap().put(name, getConverter().formatInt(value));
    }

    /**
     * Get an int value. The string value is parsed using the supplied getConverter(). Returns 0 if there
     * is no entry for the name, or it is null.
     */
    public int getInt(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            try {
                return getConverter().parseInt(stringValue);
            } catch (ValueConvertException e) {
                CsServices.getDefaultErrorHandler().handleError(e, StringMap.class);
                throw new ValueConvertException(name, Integer.TYPE, stringValue, e);
            }
        }
        return 0;
    }
    
    private void copyIntegerToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Integer intValue = (Integer) method.invoke(bean, args);
        if (intValue != null) {
            stringValue = getConverter().formatInt(intValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    protected boolean isTypeInteger(Class<?> type) {
        return type == Integer.TYPE || type == Integer.class;
    }
    
    // Short
    
    /**
     * Set a short value. The value is formatted using the supplied getConverter().
     */
    public void putShort(String name, short value) {
        getMap().put(name, getConverter().formatShort(value));
    }

    /**
     * Get a short value. The string value is parsed using the supplied getConverter(). Returns 0 if
     * there is no entry for the name, or it is null.
     */
    public short getShort(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            try {
                return getConverter().parseShort(stringValue);
            } catch (ValueConvertException e) {
                CsServices.getDefaultErrorHandler().handleError(e, StringMap.class);
                throw new ValueConvertException(name, Short.TYPE, stringValue, e);
            }
        }
        return 0;
    }
    
    private void copyShortToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Short shortValue = (Short) method.invoke(bean, args);
        if (shortValue != null) {
            stringValue = getConverter().formatShort(shortValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    private boolean isTypeShort(Class<?> type) {
        return type == Short.TYPE || type == Short.class;
    }
    
    // Long
    
    private void copyLongToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Long longValue = (Long) method.invoke(bean, args);
        if (longValue != null) {
            stringValue = getConverter().formatLong(longValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }

    private boolean isTypeLong(Class<?> type) {
        return type == Long.TYPE || type == Long.class;
    }
}
