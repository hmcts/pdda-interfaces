package uk.gov.hmcts.framework.services.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.security.SubjectManager;

import java.security.PrivilegedActionException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LazyLoadingServiceLocatorImpl.

 * @author Meeraj
 * @title LazyLoadingServiceLocatorImpl.
 */
public class LazyLoadingServiceLocatorImpl extends ServiceLocatorImpl {

    /** Logger instance. */
    private static Logger log = LoggerFactory.getLogger(LazyLoadingServiceLocatorImpl.class);

    private final Map<String, Object> jndiCache =
        Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     * Utility method to lookup objects.

     * @param jndiName String 
     * @return Object
     */
    @Override
    protected Object lookup(String jndiName) {
        if (!jndiCache.containsKey(jndiName)) {
            LookupHelper lookupHelper = new LookupHelper(getEnv(), jndiName);
            log.info("LookupHelper::{}", lookupHelper);
            try {
                Object obj = SubjectManager.getInstance().runAs(lookupHelper);
                jndiCache.put(jndiName, obj);
            } catch (PrivilegedActionException ex) {
                throw handleException(ex);
            }
        }
        return jndiCache.get(jndiName);
    }

}
