package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Common superclass for public display data classes.
 * 
 * @author pznwc5
 */
@SuppressWarnings("PMD.LawOfDemeter")
public abstract class AbstractValue implements Serializable {

    static final long serialVersionUID = 4294442881965374830L;

    private static final String EQUAL_SIGN = "=";
    private static final String SEMICOLON_SIGN = ";";
    private static final String CLASS = "class";

    /**
     * Date formatter.
     */
    protected static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("HH:mm");

    protected static final String IS_FLOATING = "1";

    /**
     * Implements pretty print.
     * 
     * @return Pretty print string
     * 
     * @throws CsUnrecoverableException Exception
     */
    @Override
    public String toString() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            Arrays.sort(pds, (o1, o2) -> o1.getName().compareTo(o2.getName()));

            StringBuilder sb = new StringBuilder();

            for (PropertyDescriptor pd : pds) {
                String propertyName = getPropertyName(pd);
                if (propertyName != null) {
                    Object[] args = {};
                    Object value = pd.getReadMethod().invoke(this, args);
                    if (value != null) {
                        if (value.getClass().isArray()) {
                            sb.append(propertyName).append(EQUAL_SIGN).append(Arrays.asList((Object[]) value))
                                .append(SEMICOLON_SIGN);
                        } else {
                            sb.append(propertyName).append(EQUAL_SIGN).append(value).append(SEMICOLON_SIGN);
                        }
                    }
                }
            }

            return sb.toString();
        } catch (Exception e) {
            throw new CsUnrecoverableException(e);
        }
    }

    public abstract boolean hasInformationForDisplay();

    private String getPropertyName(PropertyDescriptor pds) {
        String propertyName = pds.getName();
        if (!CLASS.equals(propertyName)) {
            Method readMethod = pds.getReadMethod();
            if (readMethod != null) {
                return propertyName;
            }
        }
        return null;
    }
}
