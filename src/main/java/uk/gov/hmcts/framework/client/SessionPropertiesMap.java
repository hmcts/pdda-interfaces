package uk.gov.hmcts.framework.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: Implementation of HashMap with strongly typed get and put methods.
 * </p>
 * <p>
 * Description: This is a suggested class to use when implementing CSUserSession. to store the
 * session properties. You will need to implement SessionPropertyKey. This will allow you to define
 * a strongly typed key for the map.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: SessionPropertiesMap.java,v 1.3 2006/06/05 12:30:14 bzjrnl Exp $
 */
public class SessionPropertiesMap implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<SessionPropertyKey, Object> map = new ConcurrentHashMap<>();

    public Object get(SessionPropertyKey key) {
        return map.get(key);
    }

    public Object put(SessionPropertyKey key, Object value) {
        return map.put(key, value);
    }

    public boolean containsKey(SessionPropertyKey key) {
        return map.containsKey(key);
    }

    public Object remove(SessionPropertyKey key) {
        return map.remove(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public int size() {
        return map.size();
    }

    public Set<SessionPropertyKey> keySet() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }
}
