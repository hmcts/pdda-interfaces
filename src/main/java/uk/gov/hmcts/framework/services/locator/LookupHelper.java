package uk.gov.hmcts.framework.services.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkException;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


/**
 * This is a privileged class for performing JNDI lookup.
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
public class LookupHelper implements PrivilegedExceptionAction<Object> {

    /** Eager load mode. */
    private static final int EAGER = 0;

    /** Lazy load mode. */
    private static final int LAZY = 1;

    /** Logger instance. */
    private static Logger log = LoggerFactory.getLogger(LookupHelper.class);

    /** Root context. */
    private static final String ROOT_CONTEXT = "";

    /** JNDI environment. */
    private final Properties env;

    /** Loading mode. */
    private final int mode;

    /** JNDI name. */
    private String jndiName;

    /**
     * Initializes the JNDI environment.
     * 
     * @param newEnv Properties
     */
    public LookupHelper(Properties newEnv) {
        env = newEnv;
        mode = EAGER;
    }

    /**
     * Initializes the JNDI environment.
     * 
     * @param newEnv Properties
     * @PARAM newJndiName String
     */
    public LookupHelper(Properties newEnv, String newJndiName) {
        env = newEnv;
        jndiName = newJndiName;
        mode = LAZY;
    }

    /**
     * PrivilegedExceptionAction implementation.
     * 
     * @return Object
     * @throws NamingException Exception
     */
    @Override
    public Object run() throws NamingException {
        switch (mode) {
            case EAGER:
                return eagerLoad();
            case LAZY:
                return lookup();
            default:
                throw new IllegalArgumentException("Unknown mode");
        }
    }

    /**
     * Eager loads the JNDI tree.
     * 
     * @return HashMap
     * @throws NamingException Exception
     */
    private Map<String, Object> eagerLoad() throws NamingException {
        InitialContext context = null;
        try {
            context = new InitialContext(env);
            return listContext(context, ROOT_CONTEXT);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /**
     * Looks up the object.
     * 
     * @return Object
     * @throws NamingException Exception
     */
    private Object lookup() throws NamingException {
        InitialContext context = null;
        try {
            context = new InitialContext(env);
            return context.lookup(jndiName);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /**
     * This method recursively looks up the objects bound in the JNDI tree.
     * 
     * @param ctx context
     * @param name String
     * @return HashMap of name-to-object bindings in the sub context
     * @throws NamingException Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, Object> listContext(Context ctx, String name) throws NamingException {

        // List the sub context
        NamingEnumeration enumeration = ctx.list(name);
        Map bindings = new ConcurrentHashMap<>();

        // Iterate through the list
        while (enumeration.hasMoreElements()) {

            // Get the binding name and the class name
            NameClassPair pair = (NameClassPair) enumeration.nextElement();
            String bindingName = pair.getName();
            String bindingClassName = pair.getClassName();

            // Recursively add bindins for sub contexts
            if (isContext(bindingClassName)) {
                bindings.putAll(listContext(ctx, name + bindingName + "/"));
            } else {
                // If binding is not a sub-context cache it
                String jndiName = name + bindingName;
                try {
                    Object obj = ctx.lookup(jndiName);
                    bindings.put(jndiName, obj);
                    log.debug("Got binding: {}", jndiName);
                } catch (LinkException ex) {
                    // We don't want local references in remote clients
                    log.info("Name not found (LinkException):{}", jndiName);
                } catch (NoClassDefFoundError ex) {
                    /*
                     * Weblogic JNDI provider throws a NoClassDefFoundError when the classes required for the looked up
                     * stub are not available in the classpath. The only reason we are catching this to prevent service
                     * locator initialization from failing When trying to lookup a stub that is not required. If it is a
                     * required stub and the error is thrown, the client will still get an exception when the locator is
                     * used to get a reference to the stub, as the stub won't be present in the locator cache
                     */
                    log.info("Name not found (NoClassDefFoundError):{}", jndiName);
                }
            }
        }
        return bindings;
    }

    /**
     * Utility method to check whether a given class is a Context.
     */
    private boolean isContext(String bindingClassName) {

        try {
            Class<?> bindingClass = Class.forName(bindingClassName);
            return Context.class.isAssignableFrom(bindingClass);
        } catch (ClassNotFoundException ex) {
            return false;
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("jndiName::").append(jndiName).append("env::").append(env.toString());
        return sb.toString();
    }

}
