package uk.gov.hmcts.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/*
 * TO DO Check if error handling is consistent with other framework classes.
 */

/**
 * This class is used for help when using reflection to set data on objects.
 */
public class ClassAndMethodHelper {
    // The logger for this class.
    private static Logger log = LoggerFactory.getLogger(ClassAndMethodHelper.class);
    private static final String METHOD_NAME = " methodName: ";
    private static final String METHOD_FOR = " method for: ";
    private static final String WITH_PARAMETER = " with parameter: ";

    private static final String CONFIG_ERR_MSG = " probably config error in progressPropertyMapping.txt or ";
    private static final String MISMATCH_JMS_ERR_MSG =
        " mismatch between JMS structure (ie database schema) and Web Service data object structure";
    private static final String FAILED_PARAM = "Failed to create parameter for class: ";
    private static final String PASSING_CONSTRUCTOR = " passing the constructor value class: ";
    private static final Integer ONE = 1;

    /**
     * Generic method to execute a method on an executor object passing the specified parameter.
     *
     * @param parameter Object
     * @param methodName Striing
     * @param executor Object
     * @return Object
     */
    public Object execute(Object parameter, String methodName, Object executor) {
        log.debug("***** execute parameter: " + parameter.getClass() + METHOD_NAME + methodName);

        Class<?> clazz = getExeceutionClass(parameter);

        Class<?>[] methodParamTypes = {clazz};

        Method method = getExeceutionMethod(parameter, methodName, executor, methodParamTypes);

        Object[] parameters = {parameter};
        try {
            return method.invoke(executor, parameters);
        } catch (IllegalAccessException iae) {
            log.debug("<< IllegalAccessException executing on class : " + executor.getClass() + WITH_PARAMETER
                + parameter.getClass() + METHOD_NAME + methodName);

            CsServices.getDefaultErrorHandler().handleError(iae, ClassAndMethodHelper.class);

            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(
                "IllegalAccessException when invoking: " + methodName + METHOD_FOR + parameter.getClass(), iae);
        } catch (IllegalArgumentException iarge) {
            log.debug("<< IllegalArgumentException executing on class : " + executor.getClass() + WITH_PARAMETER
                + parameter.getClass() + METHOD_NAME + methodName);

            CsServices.getDefaultErrorHandler().handleError(iarge, ClassAndMethodHelper.class);

            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(
                "IllegalArgumentException when invoking: " + methodName + METHOD_FOR + parameter.getClass(), iarge);
        } catch (InvocationTargetException ite) {
            log.debug("<< InvocationTargetException executing on class : " + executor.getClass() + WITH_PARAMETER
                + parameter.getClass() + METHOD_NAME + methodName);

            CsServices.getDefaultErrorHandler().handleError(ite, ClassAndMethodHelper.class);

            log.debug("InvocationTargetException (RMI Exception) when invoking: " + methodName + METHOD_FOR
                + parameter.getClass() + " Exception was: " + ite);

            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException("InvocationTargetException (RMI Exception) when invoking: " + methodName
                + METHOD_FOR + parameter.getClass(), ite);
        } catch (Exception e) {
            log.debug("<< Exception executing on class : " + executor.getClass() + WITH_PARAMETER + parameter.getClass()
                + METHOD_NAME + methodName);

            CsServices.getDefaultErrorHandler().handleError(e, ClassAndMethodHelper.class);

            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(
                "Exception when invoking: " + methodName + METHOD_FOR + parameter.getClass(), e);
        }
    }

    private Class<?> getExeceutionClass(Object parameter) {
        if (parameter.getClass().isAssignableFrom(java.util.GregorianCalendar.class)) {
            log.debug(
                "***** parameter is GregorianCalendar so set the method parm to Calendar : " + parameter.getClass());
            return java.util.Calendar.class;
        } else {
            return parameter.getClass();
        }
    }

    public Method getExeceutionMethod(Object parameter, String methodName, Object executor,
        Class<?>... methodParamTypes) {
        try {
            return executor.getClass().getMethod(methodName, methodParamTypes);
        } catch (NoSuchMethodException nsme) {
            log.debug("<< NoSuchMethodExceptionexecute executing on class : " + executor.getClass() + WITH_PARAMETER
                + parameter.getClass() + METHOD_NAME + methodName);

            CsServices.getDefaultErrorHandler().handleError(nsme, ClassAndMethodHelper.class);

            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(
                "No Such Method Exception when creating: " + methodName + METHOD_FOR + parameter.getClass(), nsme);
        }
    }

    /**
     * determineMethodParameter: This returns an Object which will be passed as a parameter to set the
     * appropriate property data structure.
     *
     * <p>
     * This method is quite specialized and the following must be true: 1. The Class argument has
     * exactly one method with a name matching the methodName argument 2. The method matching the
     * methodName argument has itself exactly one argument 3. The original property class determined
     * from the propertyValue argument will be one of: (a) Be an instance of the method argument in (2)
     * (b) Be applicable to be passed into a single argument constructor of the method argument's class
     * (c) Be a formatted (Oracle dates only) Date String when the method argument is a Calendar
     * </p>
     *
     * @param clazz Class
     * @param methodName String
     * @param propertyValue String
     * @return Object
     */

    public Object determineMethodParameter(Class<?> clazz, String methodName, String propertyValue) {
        log.debug("determineMethodParameter(methodName: {}, propertyValue: {})", methodName, propertyValue);

        Method[] methodArray = clazz.getMethods();

        Method method;
        Class<?>[] classArray;
        Class<?> methodParameterClass = null;

        // Assumes the class will have exactly one method with the specified name and exactly one
        // parameter
        // Otherwise we could hold the expected parameter type in the progressPropertyMapping
        // instead

        for (int i = 0; i < Array.getLength(methodArray); i++) {
            method = methodArray[i];

            if (method.getName().trim().equals(methodName.trim())) {
                log.debug("Method match found: {}", methodName);
                classArray = method.getParameterTypes();

                if (Array.getLength(classArray) == ONE) {
                    log.debug("Appropriate parameter found");
                    methodParameterClass = classArray[0];
                    break;
                }
            }
        }

        if (methodParameterClass == null) {
            log.debug(
                "determineMethodParameter, methodParameterClass was null: " + CONFIG_ERR_MSG + MISMATCH_JMS_ERR_MSG);

            // this is an configuration exception, not a business exception
            throw new CsUnrecoverableException("Failed to determine parameter class for method: " + methodName
                + " on Class class: " + clazz + CONFIG_ERR_MSG + MISMATCH_JMS_ERR_MSG);
        }

        log.debug("determineMethodParameter, methodParameterClass:{}", methodParameterClass.getName());

        final Class<? extends String> propertyValueClass = propertyValue.getClass();

        log.debug("determineMethodParameter, propertyValueClass:{}", propertyValueClass.getName());

        if (propertyValueClass.isAssignableFrom(methodParameterClass)) {
            log.debug(
                "<< propertyValueClass is an instance of methodParameterClass so it can be used as a parameter >>");
            return propertyValue;
        }

        if (methodParameterClass.isAssignableFrom(java.util.Calendar.class)) {
            log.debug("<< methodParameterClass is a Calendar >>");
            getMethodParameterCalendar(methodParameterClass, propertyValue);
        }

        if (methodParameterClass.isAssignableFrom(java.util.Date.class)) {
            log.debug("<< methodParameterClass is a Date >>");
            return getMethodParameterDate(methodParameterClass, propertyValue);
        }

        return getMethodParameterString(methodParameterClass, propertyValue);
    }
    
    private Object getMethodParameterCalendar(Class<?> methodParameterClass, String propertyValue) {
        try {
            return DateTimeUtilities.processOracleDateParameter(propertyValue);
        } catch (Exception e) {
            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(FAILED_PARAM + methodParameterClass + PASSING_CONSTRUCTOR
                + propertyValue.getClass() + " due to an error with the Calendar (either it was null or did not "
                + " follow the Oracle Timestamp format)", e);
        }
    }
    
    private Object getMethodParameterDate(Class<?> methodParameterClass, String propertyValue) {
        try {
            return DateTimeUtilities.processOracleDateParameterForDate(propertyValue);
        } catch (Exception e) {
            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(FAILED_PARAM + methodParameterClass + PASSING_CONSTRUCTOR
                + propertyValue.getClass() + " due to an error with the Date (either it was null or did not "
                + " follow the Oracle Timestamp format)", e);
        }
    }
    
    private Object getMethodParameterString(Class<?> methodParameterClass, String propertyValue) {
        try {
            log.debug("<< Construct object of methodParameterClass using propertyValue as parameter  >>");

            // methodParameterClass must have a constructor that takes a propertyValueClass as a
            // single
            // parameter

            // Array of classes containing the type of parameter to be passed to the constructor,
            // which
            // will always be a String
            Class<?>[] constructorParmsClasses = {String.class};

            Constructor<?> methodParameterConstructor = methodParameterClass.getConstructor(constructorParmsClasses);
            log.debug("Created constructor of parameter:{}", methodParameterConstructor.getName());

            Object[] constructorParms = {propertyValue};
            return methodParameterConstructor.newInstance(constructorParms);

        } catch (Exception e) {
            // this is an unexpected exception, not a business exception
            throw new CsUnrecoverableException(FAILED_PARAM + methodParameterClass + PASSING_CONSTRUCTOR
                + propertyValue.getClass() + CONFIG_ERR_MSG + MISMATCH_JMS_ERR_MSG, e);
        }
    }
}
