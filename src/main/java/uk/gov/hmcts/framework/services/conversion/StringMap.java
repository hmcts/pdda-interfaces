package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.exception.CsException;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


/**
 * A map from Strings to Strings. Basically, a Properties, with extra methods including parsing,
 * formating, and bean introspection.
 * 
 * @author Nick Lawson
 *         Pete Raymond Originally written for workstep by Nick Lawson this class has been updated
 *         and ammended by Pete Raymond
 * 
 */
@SuppressWarnings("PMD.LooseCoupling")
public class StringMap extends ByteMap {

    public StringMap(ValueConverter converter) {
        super(converter);
    }

    /**
     * Synonym for putString().
     */
    public void put(String name, String value) {
        getMap().put(name, value);
    }

    /**
     * Synonym for getString().
     */
    public String get(String name) {
        String stringValue = (String) getMap().get(name);
        if (stringValue != null) {
            return stringValue;
        } else {
            return "";
        }
    }

    public List<?> getIndexedStrings(String name) {
        return (ArrayList<?>) getMap().get(name);
    }

    public List<?> getValuesAsMaps(List<?> values) {
        putList("tmpKey", values);
        return getInnerMaps("tmpKey");
    }

    public List<?> getInnerMaps(String name) {
        Object obj = getMap().get(name);
        if (obj != null) {
            return (ArrayList<?>) obj;
        } else {
            return new ArrayList<>(0);
        }
    }

    @SuppressWarnings("unchecked")
    public void putIndexedString(String name, String value) {
        // check if there is already an arraylist for this name
        ArrayList stringValues = (ArrayList) getMap().get(name);
        if (stringValues == null) {
            stringValues = new ArrayList<>();
        }
        stringValues.add(value);
        getMap().put(name, stringValues);
    }

    /**
     * Put a bean's properties.
     * 
     * <p>This copies properties from the bean to here. Same as
     * {@link #putBeanProperties(java.lang.Object,java.lang.String) putBeanProperties()} with a
     * zero-length prefix.
     * 
     * @throws CsException if an Exception is thrown by the bean's getter method.
     */
    public void copyValueToMap(Object bean) {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?>[] types = method.getParameterTypes();
            int modifier = method.getModifiers();
            if (methodName.startsWith("get") && methodName.length() > 3
                && Modifier.isPublic(modifier) && types.length == 0) {

                String propertyName =
                    Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                Class<?> type = method.getReturnType();

                copyTypeToMap(type, propertyName, method, bean);
            }
        }
    }

    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeString(type)) {
                copyStringToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, StringMap.class);
            throw ex;
        }
    }

    private boolean isTypeString(Class<?> type) {
        return type == String.class;
    }

    private void copyStringToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        String stringValue = (String) method.invoke(bean, args);
        getMap().put(propertyName, stringValue);
    }
}
