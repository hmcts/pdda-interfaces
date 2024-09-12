package uk.gov.hmcts.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * Title: Sorter.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Meeraj Kunnumpurath
 * @version $Id: Sorter.java,v 1.12 2006/07/14 10:26:06 bzjrnl Exp $
 */
/*
 * Ref Date Author Description
 * 
 * 315, 02-04-2003 AW Daley Sort modified to provide ascending 52305 or descending order. compareTo
 * method can now be on a superclass of the object to be compared.
 * 
 * 83,52539 29-04-2003 AW Daley Modifed to use ReflectionHelper class.
 * 
 * 30-10-2003 AW Daley compare method modified to allow objects to be sorted that contain null
 * member attributes.
 */
public final class Sorter {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(Sorter.class);

    public static final Boolean ASCENDING = Boolean.TRUE;

    public static final Boolean DESCENDING = Boolean.FALSE;

    /**
     * Private constructor for the utility class.
     */
    private Sorter() {
    }

    /**
     * Statis utility method to sort data.
     * 
     * @param data Data to be sorted
     * @param keys Keys by which to be sorted
     */
    public static void sort(List<?> data, final String... keys) {

        Comparator<Object> cmp = new Sorter.ReflectionComparator(keys);

        Collections.sort(data, cmp);

    }

    /**
     * Statis utility method to sort data.
     * 
     * @param data Data to be sorted
     * @param keys Keys by which to be sorted
     */
    public static void sort(Object[] data, final String... keys) {

        Comparator<Object> cmp = new Sorter.ReflectionComparator(keys);

        Arrays.sort(data, cmp);

    }

    /**
     * Statis utility method to sort data.
     * 
     * @param data Array of Data to be sorted
     * @param keys Keys by which to be sorted
     * @param ascending True - ascending order
     */
    public static void sort(Object[] data, final String[] keys, Boolean ascending) {
        Comparator<Object> cmp = new Sorter.ReflectionComparator(keys, ascending);

        Arrays.sort(data, cmp);
    }

    /**
     * Statis utility method to sort data.
     * 
     * @param data Collection of Data to be sorted
     * @param keys Keys by which to be sorted
     * @param ascending True - ascending order
     */
    public static void sort(List<Object> data, final String[] keys, Boolean ascending) {
        Comparator<Object> cmp = new Sorter.ReflectionComparator(keys, ascending);

        Collections.sort(data, cmp);
    }

    /**
     * Comparator implementation based on reflection.
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * <p>
     * Copyright: Copyright (c) 2002
     * </p>
     * <p>
     * Company: EDS
     * </p>
     * 
     * @author Meeraj Kunnumpurath
     * @version 1.0
     */
    private static class ReflectionComparator implements Comparator<Object> {

        /**
         * Keys by which to sort.
         */
        private final String[] keys;

        /**
         * Specifies ascending or descending order.
         */
        private Boolean ascending;

        /**
         * Constructor initalises the sort keys.
         * 
         * @param keys StringArray
         */
        public ReflectionComparator(String... keys) {
            this(keys, Boolean.TRUE);
        }

        /**
         * Constructor initializes the sort keys.
         * 
         * @param ascending True - ascending
         */
        public ReflectionComparator(String[] keys, Boolean ascending) {
            this.keys = keys.clone();
            setAscending(ascending);
        }

        /**
         * Sets ascending or descending order.
         * 
         * @param ascending True - ascending
         */
        public final void setAscending(Boolean ascending) {
            this.ascending = ascending;
        }

        /**
         * Gets ascending or descending order.
         * 
         * @return True - ascending
         */
        public boolean getAscending() {
            return this.ascending;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }

        @Override
        public int hashCode() {
            LOG.debug("hashCode()");
            return super.hashCode();
        }

        /**
         * Compare method based on the compareTo implementation.
         * 
         * @param obj1 Object
         * @param obj2 Object
         * @return int
         */
        @Override
        public int compare(Object obj1, Object obj2) {
            try {
                for (String key : keys) {
                    // Get the 2 objects to compare on...
                    Object res1 = getObject(obj1, key);
                    Object res2 = getObject(obj2, key);

                    // If both objects are null then they are equal
                    if (res1 == null && res2 == null) {
                        continue;

                        // If one object is null and the other not then
                        // they are not equal
                    } else if (res1 == null) {
                        return adjustCompareResult(-1);

                    } else if (res2 == null) {
                        return adjustCompareResult(1);
                    }
                    /*
                     * Note compareTo can be on the class itself, or its superclasses
                     */
                    Integer result = getResult(res1, res2);
                    if (result != 0) {

                        // If descending then reverse sign on result
                        return adjustCompareResult(result);
                    }
                }

                return 0;
            } catch (Exception ex) {
                CsServices.getDefaultErrorHandler().handleError(ex, getClass(), ex.toString());
                throw new CsUnrecoverableException(ex);
            }
        }

        private Integer getResult(Object res1, Object res2)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Method compareTo = ReflectionHelper.getMethodFromClassHierarchy(res1.getClass(),
                "compareTo", getClassArray());

            if (compareTo == null) {
                throw new NoSuchMethodException("compare() - ");
            }
            Object[] args = {res2};
            return (Integer) compareTo.invoke(res1, args);
        }

        private Class[] getClassArray() {
            return new Class[] {Object.class};
        }

        private Object getObject(Object obj, String key)
            throws InvocationTargetException, IllegalAccessException, IntrospectionException {
            Method method = getMethod(obj, key);
            return method.invoke(obj, (Object[]) null);
        }

        private Method getMethod(Object obj, String key) throws IntrospectionException {
            PropertyDescriptor pd = new PropertyDescriptor(key, obj.getClass());
            return pd.getReadMethod();
        }

        private int adjustCompareResult(Integer result) {
            // If descending then reverse sign on result
            if (getAscending()) {
                return result;
            } else {
                return result * (-1);
            }
        }
    }
}
