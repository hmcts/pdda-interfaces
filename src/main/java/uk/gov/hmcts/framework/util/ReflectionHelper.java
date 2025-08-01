package uk.gov.hmcts.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**

 * Title: ReflectionHelper.


 * Description: Reflection Helper class that searches class hierarchies for specified methods and
 * fields.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author AW Daley
 */
/*
 * Ref Date Author Description

 * 83,52539 29-04-2003 AW Daley Initial version.
 */
public class ReflectionHelper {

    private static final Integer ZERO = 0;
    private static final Integer ONE = 1;
    
    protected ReflectionHelper() {
        // Protected constructor
    }

    /**
     * Returns a field given a path to the field.

     * @param cls The sub class in the hierarchy where the search is to begin
     * @param fieldPath The path of the field to return. A field contained in a hiearchy of objects
     *        can be returned by specifying each attribute name seperated by a delimiter. (e.g
     *        courtReporterFirm.firmName)
     * @param fieldPathDelimiter field path delimiter
     * @return Specified field
     * @throws NoSuchFieldException Exception
     */
    public static Field getFieldFromFieldPath(Class<?> cls, String fieldPath,
        String fieldPathDelimiter) throws NoSuchFieldException {

        // Get Each Field Name
        String fieldNamesToParse = String.valueOf(fieldPath);
        StringTokenizer tokens = new StringTokenizer(fieldNamesToParse, fieldPathDelimiter);
        // If field names not found then throw Exception
        if (tokens.countTokens() == ZERO) {
            throw new NoSuchFieldException(fieldPath);
        }
        // If only one field
        if (tokens.countTokens() == ONE) {
            return getFieldFromClassHierarchy(cls, tokens.nextToken());
        }
        // For each field
        Class<?> currentClass = cls;
        Field requiredField = null;
        while (tokens.hasMoreTokens()) {
            // Recursive
            requiredField = getFieldFromClassHierarchy(currentClass, tokens.nextToken());
            currentClass = requiredField.getType();
        }

        return requiredField;

    }

    /**
     * Returns the getter method for a field given the path to the field.

     * @param fieldPath The path of the field to return. A field contained in a hiearchy of objects
     *        can be returned by specifying each attribute name separated by a period. (e.g
     *        courtReporterFirm.firmName)
     * @param fieldPathDelimiter field path delimiter
     * @return Array of Getter methods to retrieve the specified field
     * @throws NoSuchFieldException Exception
     * @throws NoSuchMethodException Exception
     */
    public static Method[] getGetterMethodForFieldPath(Class<?> cls, String fieldPath,
        String fieldPathDelimiter) throws NoSuchMethodException, NoSuchFieldException {

        // Get Each Field Name
        String fieldNamesToParse = String.valueOf(fieldPath);
        StringTokenizer tokens = new StringTokenizer(fieldNamesToParse, fieldPathDelimiter);
        // If field names not found then throw Exception
        if (tokens.countTokens() == ZERO) {
            throw new NoSuchFieldException(fieldPath);
        }
        // If only one field then return its getter method
        List<Method> requiredMethods = new ArrayList<>();
        Class<?>[] parameterTypes = {};
        String methodName;
        if (tokens.countTokens() == ONE) {
            methodName = buildGetterMethodName(tokens.nextToken());
            Method method = getMethodFromClassHierarchy(cls, methodName, parameterTypes);
            requiredMethods.add(method);

            return requiredMethods.toArray(new Method[0]);
        }

        Field requiredField;
        String lastToken;

        // For each field
        Class<?> clsType = cls;
        Class<?> declaringCls;

        while (tokens.hasMoreTokens()) {
            // Recursive
            lastToken = String.valueOf(tokens.nextToken());
            requiredField = getFieldFromClassHierarchy(clsType, lastToken);
            clsType = requiredField.getType();

            // Get RequiredMethod for field
            methodName = buildGetterMethodName(lastToken);
            declaringCls = requiredField.getDeclaringClass();
            Method method = getMethodFromClassHierarchy(declaringCls, methodName, parameterTypes);
            requiredMethods.add(method);
        }

        return requiredMethods.toArray(new Method[0]);

    }

    /**
     * Recursive method to traverse the class hierarchy searching for the specified method.

     * @param cls Class
     * @param methodName method name to search for
     * @param parameterTypes parameter types
     * @return the specified method or null if not found
     * @throws NoSuchMethodException Exception
     */
    public static Method getMethodFromClassHierarchy(Class<?> cls, String methodName,
        Class<?>... parameterTypes) throws NoSuchMethodException {
        Method requiredMethod;

        try {

            requiredMethod = cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException nme) {
            // Return null if top of class structure
            if (cls.getSuperclass() != null) {
                requiredMethod =
                    getMethodFromClassHierarchy(cls.getSuperclass(), methodName, parameterTypes);
            } else {
                throw nme;
            }
        }

        return requiredMethod;

    }

    /**
     * Recursive method to traverse the class hierarchy searching for the specified field.

     * @param cls The sub class in the hierarchy where the search is to begin
     * @param fieldName field to search for.
     * @return the specific field or null if not found
     * @throws NoSuchFieldException Exception
     */
    public static Field getFieldFromClassHierarchy(Class<?> cls, String fieldName)
        throws NoSuchFieldException {
        Field requiredField;

        try {

            requiredField = cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nme) {
            // Return null if top of class structure
            if (cls.getSuperclass() != null) {
                requiredField = getFieldFromClassHierarchy(cls.getSuperclass(), fieldName);
            } else {
                throw nme;
            }
        }

        return requiredField;

    }

    /**
     * Build getter method name for given field name.

     * @param fieldName field name for which getter method name is to be built
     * @return getter method name
     */
    private static String buildGetterMethodName(String fieldName) {
        return "get".concat(fieldName.substring(0, 1).toUpperCase(Locale.getDefault())
            .concat(fieldName.substring(1, fieldName.length())));
    }

}
