package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * @author pznwc5
 * 
 * Context for passing attributes
 */
/**
 * Represents a translation context that stores key-value attributes.
 */
public class TranslationContext {

    // Use generics and an ordered map for automatic key sorting
    private final Map<String, Object> values = new TreeMap<>();

    /**
     * Retrieves a named attribute.
     *
     * @param name the name of the attribute
     * @return the value of the attribute, or {@code null} if not present
     */
    public Object get(String name) {
        return values.get(name);
    }

    /**
     * Adds or updates a named attribute.
     *
     * @param name the name of the attribute
     * @param value the value to associate
     */
    public void put(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Returns a string representation of the translation context.
     *
     * @return a string listing all key-value pairs
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "TranslationContext[", "]");
        values.forEach((k, v) -> joiner.add(k + "=" + Objects.toString(v)));
        return joiner.toString();
    }
}

