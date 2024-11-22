package uk.gov.hmcts.pdda.courtlog.xsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Translation context.
 * 
 * @author pznwc5
 * 
 *         Context for passing attributes
 */
public class TranslationContext {

    private static final Integer STRING_BUFFER_SIZE = 23;
    
    // Map of values
    private final Map vals = new ConcurrentHashMap<>();

    /**
     * Gets a named attribute.
     * 
     * @param name Name of the attribute
     * @return Value of the attribute
     */
    public Object get(String name) {
        return vals.get(name);
    }

    /**
     * Puts a named attribute.
     * 
     * @param name Name of the attribute
     * @param val Value of the attribute
     */
    @SuppressWarnings("unchecked")
    public void put(String name, Object val) {
        vals.put(name, val);
    }

    /**
     * Return a string representation of the translation context.
     */
    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(STRING_BUFFER_SIZE);
        buffer.append("TranslationContext[");
        List keyList = new ArrayList<>();
        keyList.addAll(vals.keySet());
        Collections.sort(keyList);
        Iterator keys = keyList.iterator();
        if (keys.hasNext()) {
            Object key = keys.next();
            buffer.append(key).append('=').append(vals.get(key));
            while (keys.hasNext()) {
                key = keys.next();
                buffer.append(',').append(key).append('=').append(vals.get(key));
            }
        }
        buffer.append(']');
        return buffer.toString();
    }
}
