package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BooleanMap extends DateMap {

    public BooleanMap(ValueConverter converter) {
        super(converter);
    }
    

    /**
     * Set a boolean value. The boolean value is formatted using the supplied getConverter().
     */
    public void putBoolean(String name, boolean value) {
        getMap().put(name, getConverter().formatBoolean(value));
    }

    /**
     * Get a boolean value. The string value is parsed using the supplied getConverter(). Returns false
     * if there is no entry for the name, or it is null.
     */
    public boolean getBoolean(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            try {
                return getConverter().parseBoolean(stringValue);
            } catch (ValueConvertException e) {
                CsServices.getDefaultErrorHandler().handleError(e, StringMap.class);
                throw new ValueConvertException(name, Boolean.TYPE, stringValue, e);
            }
        }
        return false;
    }
    
    private void copyBooleanToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Boolean boolValue = (Boolean) method.invoke(bean, args);
        if (boolValue != null) {
            stringValue = getConverter().formatBoolean(boolValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    private boolean isTypeBoolean(Class<?> type) {
        return type == Boolean.TYPE || type == Boolean.class;
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeBoolean(type)) {
                copyBooleanToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, BooleanMap.class);
            throw ex;
        }
    }
}
