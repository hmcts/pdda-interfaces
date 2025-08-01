package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExtraNumericMap extends ValueConverterMap {

    public ExtraNumericMap(ValueConverter converter) {
        super(converter);
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeFloat(type)) {
                copyFloatToMap(propertyName, method, bean);
            } else if (isTypeDouble(type)) {
                copyDoubleToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, BasicNumericMap.class);
            throw ex;
        }
    }
    
    // Double
    
    private void copyDoubleToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Double doubleValue = (Double) method.invoke(bean, args);
        if (doubleValue != null) {
            stringValue = getConverter().formatDouble(doubleValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    private boolean isTypeDouble(Class<?> type) {
        return type == Double.TYPE || type == Double.class;
    }
    
    // Float
    
    private void copyFloatToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Float floatValue = (Float) method.invoke(bean, args);
        if (floatValue != null) {
            stringValue = getConverter().formatFloat(floatValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    private boolean isTypeFloat(Class<?> type) {
        return type == Float.TYPE || type == Float.class;
    }
}
