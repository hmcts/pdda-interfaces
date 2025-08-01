package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ByteMap extends CharMap {

    public ByteMap(ValueConverter converter) {
        super(converter);
    }
    
    /**
     * Set a byte value. The value is formatted using the supplied getConverter().
     */
    public void putByte(String name, byte value) {
        getMap().put(name, getConverter().formatByte(value));
    }

    /**
     * Get an byte value. The string value is parsed using the supplied getConverter(). Returns 0 if
     * there is no entry for the name, or it is null.
     */
    public byte getByte(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            try {
                return getConverter().parseByte(stringValue);
            } catch (ValueConvertException e) {
                CsServices.getDefaultErrorHandler().handleError(e, StringMap.class);
                throw new ValueConvertException(name, Byte.TYPE, stringValue, e);
            }
        }
        return 0;
    }
    
    private void copyByteToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue;
        Byte byteValue = (Byte) method.invoke(bean, args);
        if (byteValue != null) {
            stringValue = getConverter().formatByte(byteValue);
        } else {
            stringValue = NULL_STRING;
        }
        getMap().put(propertyName, stringValue);
    }
    
    private boolean isTypeByte(Class<?> type) {
        return type == Byte.TYPE || type == Byte.class;
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeByte(type)) {
                copyByteToMap(propertyName, method, bean);
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
