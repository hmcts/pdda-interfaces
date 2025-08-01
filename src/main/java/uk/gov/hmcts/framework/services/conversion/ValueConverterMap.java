package uk.gov.hmcts.framework.services.conversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class ValueConverterMap {

    private static final Logger LOG = LoggerFactory.getLogger(ValueConverterMap.class);

    protected static final String NULL_STRING = "null";
    protected static final String SEMI_COLON = ";";

    protected static final Integer ONE = 1;

    private final Map<String, Serializable> map = new ConcurrentHashMap<>();

    private final ValueConverter converter;

    public ValueConverterMap(ValueConverter converter) {
        this.converter = converter;
    }

    protected ValueConverter getConverter() {
        return converter;
    }

    protected Map<String, Serializable> getMap() {
        return map;
    }

    /**
     * Synonym for getString().
     */
    public String getIndexedString(String name, int index) {
        List<?> stringValues = (ArrayList<?>) getMap().get(name);
        String value = null;
        if (stringValues != null && index <= stringValues.size()) {
            value = (String) stringValues.get(index);
        }
        if (value != null) {
            return value;
        } else {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public void addInnerMap(String name, StringMap innerMap) {
        Object obj = getMap().get(name);
        List<Object> inMaps;
        if (obj == null) {
            inMaps = new ArrayList<>();
        } else {
            inMaps = (ArrayList) obj;
        }
        inMaps.add(innerMap);
        getMap().put(name, (ArrayList) inMaps);
    }

    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        LOG.debug("Type not found");
    }

    /*
     * Returns a ordered string in form of "field=value;.......;field=value"
     */
    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        Iterator<?> entries = getMap().entrySet().iterator();
        Set<String> treeSet = new TreeSet<>();

        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            treeSet.add(entry.getKey() + "=" + entry.getValue());
        }

        Iterator elements = treeSet.iterator();
        while (elements.hasNext()) {
            buf.append((String) elements.next()).append(SEMI_COLON);
        }

        return buf.toString();
    }

    /**
     * Show all names and vos on the printer.
     */
    public void list(PrintWriter writer) {
        Iterator<?> entries = getMap().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            writer.println("\t" + entry.getKey() + ": \"" + entry.getValue() + "\"");
        }
    }

    /**
     * Show all names and vos on the printer.
     */
    public void list(PrintStream stream) {
        list(new PrintWriter(stream, true));
    }

    /**
     * Show all names and vos on System.out.
     */
    public void list() {
        list(new PrintWriter(System.out, true));
    }
}
