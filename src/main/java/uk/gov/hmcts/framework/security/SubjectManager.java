package uk.gov.hmcts.framework.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.security.exception.SubjectManagerException;
import uk.gov.hmcts.framework.services.CsServices;

import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;


/**

 * Title: AccessInfoFactory.


 * Description: Concrete instances of this class are found using the discovery pattern.


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: SubjectManager.java,v 1.4 2014/06/20 18:03:08 atwells Exp $
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public abstract class SubjectManager {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(SubjectManager.class);

    private static SubjectManager instance;

    /**
     * Get the singleton using the discovery pattern.

     * @return the singleton instance.
     */
    public static SubjectManager getInstance() {
        synchronized (SubjectManager.class) {
            if (instance == null) {
                instance = createSubjectManager();
            }
            return instance;
        }
    }

    /**
     * Runs a privileged exception action as the current subject.

     * @param action PrivilegedExceptionAction
     * @return Object
     * @throws PrivilegedActionException Exception
     */
    public Object runAs(PrivilegedExceptionAction<?> action) throws PrivilegedActionException {
        return runAs(getCurrentSubject(), action);
    }

    /**
     * Runs a privileged action.

     * @param action PrivilegedAction
     * @return Object
     */
    public Object runAs(PrivilegedAction<?> action) {
        return runAs(getCurrentSubject(), action);
    }

    /**
     * Runs a privileged exception action as the current subject.

     * @param user Subject
     * @param action PrivilegedExceptionAction
     * @return Object
     * @throws PrivilegedActionException Exception
     */
    public Object runAs(Subject user, PrivilegedExceptionAction<?> action)
        throws PrivilegedActionException {
        LOG.debug("runAs(" + user + ")");
        try {
            return action.run();
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        }
    }

    /**
     * Runs a privileged action.

     * @param user the subject to use
     * @param action the action to execute
     * @return the value from the action
     */
    public Object runAs(Subject user, PrivilegedAction<?> action) {
        return action.run();
    }

    /**
     * Get the current current subject.
     */
    public abstract Subject getCurrentSubject();

    /**
     * Use the discovery pattern to discover a new instance of the subject manager.

     * @return a new instance of the subject manager
     * @throws SubjectManagerException if an error occures
     */
    private static SubjectManager createSubjectManager() {
        SubjectManager subjectManager =
            (SubjectManager) CsServices.getDiscoveryServices().createInstance(SubjectManager.class);
        if (subjectManager != null) {
            return subjectManager;
        }
        throw new SubjectManagerException(
            "Could not discover a concrete implementation of SubjectManager.");
    }
}
