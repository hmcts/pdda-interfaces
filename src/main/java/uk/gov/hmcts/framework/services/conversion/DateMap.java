package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class DateMap extends ListMap {

    public DateMap(ValueConverter converter) {
        super(converter);
    }
    

    /**
     * Set a Date value. The Date value is formatted using the supplied getConverter().
     */
    public void putDate(String name, Date value) {
        getMap().put(name, getConverter().formatDate(value));
    }

    /**
     * Get a Date value. The string value is parsed using the supplied getConverter(). Returns null if
     * there is no entry for the name, or it is null.
     */
    public Date getDate(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            try {
                return getConverter().parseDate(stringValue);
            } catch (ValueConvertException e) {
                CsServices.getDefaultErrorHandler().handleError(e, StringMap.class);
                throw new ValueConvertException(name, Date.class, stringValue, e);
            }
        }
        return null;
    }
    
    private void copyDateToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue = null;
        Date dateValue = (Date) method.invoke(bean, args);

        if (dateValue != null) {
            stringValue = getConverter().formatDate(dateValue);
        }
        getMap().put(propertyName, stringValue);
    }
    
    private boolean isTypeDate(Class<?> type) {
        return type == Date.class;
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeDate(type)) {
                copyDateToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, DateMap.class);
            throw ex;
        }
    }
}
