package uk.gov.hmcts.framework.services.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.security.SubjectManager;

import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Map;

/**
 * EagerLoadingServiceLocatorImpl.
 * 
 * @author Meeraj
 * @title EagerServiceLocatorImpl
 */
public class EagerLoadingServiceLocatorImpl extends ServiceLocatorImpl {

    private static final Logger LOG = LoggerFactory.getLogger(EagerLoadingServiceLocatorImpl.class);
    
    private final Map<Object, Object> jndiCache;

    /**
     * Eager loads the service locator cache.
     */
    @SuppressWarnings("unchecked")
    protected EagerLoadingServiceLocatorImpl() {
        super();
        try {
            LOG.debug("Entered into EagerLoadingServiceLocatorImpl()");
            LookupHelper lookupHelper = new LookupHelper(super.getEnv());
            jndiCache = (HashMap<Object, Object>) SubjectManager.getInstance().runAs(lookupHelper);
        } catch (PrivilegedActionException ex) {
            throw handleException(ex);
        }
    }

    /**
     * Utility method to lookup objects.
     * 
     * @param jndiName String
     * @return Object
     */
    @Override
    protected Object lookup(String jndiName) {
        if (!jndiCache.containsKey(jndiName)) {
            throw new CsUnrecoverableException("Object not found, " + jndiName);
        }
        return jndiCache.get(jndiName);
    }

}
