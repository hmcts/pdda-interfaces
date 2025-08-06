package uk.gov.hmcts.framework.services.locator;

import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This is a privileged class for creating initial contextx. I wish Java had method pointers.

 * Title:


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Meeraj Kunnumpurath
 * @version 1.0
 */
public class ContextCreator implements PrivilegedExceptionAction<Object> {

    /**
     * JNDI environment.
     */
    private final Properties env;

    /**
     * Initializes the JNDI environment.

     * @param env Properties
     */
    public ContextCreator(Properties env) {
        this.env = env;
    }

    /**
     * PrivilegedExceptionAction implementation.

     * @return Object
     * @throws NamingException Exception
     */
    @Override
    public Object run() throws NamingException {
        return getContext();
    }

    /**
     * Gets the context.

     * @return InitialContext
     * @throws NamingException Exception
     */
    private InitialContext getContext() throws NamingException {
        return new InitialContext(env);
    }

}
