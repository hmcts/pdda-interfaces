package uk.gov.hmcts.framework.services;

import jakarta.ejb.EJBLocalObject;
import jakarta.ejb.EJBObject;
import jakarta.ejb.ObjectNotFoundException;

/**
 * <p>
 * Title: EJBServices.
 * </p>
 * <p>
 * Description: Provides a group of ejb related services. Wroks with the LocatorService to find and
 * create EJBs. Provodes utility methods for copy data to and from ejb and value objects
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Pete Raymond
 * @version 1.0
 */
public interface EjbServices {

    /**
     * Creates a Session bean using the remote interface based on the JNDI name of the class. So if
     * the class DefendantControllerRemoteHome.class is passed in the JNDI name
     * DefendantControllerRemoteHome will be used as the JNDI name. The JNDI lookup will be
     * performed only from the configured subcontext for this framework application.
     * 
     * @param klass the Class file to be used for the lookup
     * @return a Session EJB representing the newly created object.
     */
    EJBObject createRemoteSession(Class<?> klass);

    /**
     * Creates a Session bean using the local interface based on the JNDI name of the class. So if
     * the class DefendantControllerHome.class is passed in the JNDI name DefendantControllerHome
     * will be used as the JNDI name. The JNDI lookup will be performed only from the configured
     * subcontext for this framework application.
     * 
     * @param klass the Class file to be used for the lookup
     * @return a Session EJB representing the newly created object.
     */
    EJBLocalObject createLocalSession(Class<?> klass);

    /**
     * Converts the serialized string into a EJBHandle then to EJBObject. Normally used to recreate
     * an ejb after a pause in the workflow.
     * 
     * @param serializedHomeHandle the String to convert to a EJBObject
     * @returns an EJBObject for the string.
     */
    EJBObject getEjbObject(String serializedHomeHandle);

    /**
     * Converts the EJBObject into a serialized string. Normally used to store an ejb during a pause
     * in the workflow.
     * 
     * @param objectToSerialize the EJBObject to convert to serialized form.
     * @returns the EJBObject serilized to String
     */
    String getSerializedEjbObject(EJBObject objectToSerialize);

    /**
     * Calculates the jndi name for a class base don the convention that each class is bound to the
     * jndi tree with the same name as its class.
     * 
     * @param ejbHome the Class to find the name for
     * @returns the String for the JNDI name
     */
    String getJndiName(Class<?> ejbHome);

    /**
     * Finds as EJB Entity using the EJBLocalHome. First finds the EJBLocalHome via the JNDi service
     * and then creates the EJBObject using the Integer primary key. For example if the the call
     * <code>findLocalEntityByPrimaryKey(DefendantHome.class, new Integer(1));</code> was made then
     * a Defendant would be returned. As the return type is EJBLocalObject this would need to be
     * cast to a Defendant.
     * 
     * @param ejbLocalHomeClass the Class to find the JNDI name from
     * @param primaryKey the Integer for the entity primary key.
     * @returns the EJBLocalObject for the for corresponding Entity. The returned object must be
     *          cast to the correct type.
     */
    EJBLocalObject findLocalEntityByPrimaryKey(Class<?> ejbLocalHomeClass,
        Integer primaryKey) throws ObjectNotFoundException;

    /**
     * Deletes an EJB Entity using the EJBLocalHome. First finds the EJBLocalHome via the JNDi
     * service and the primary key and then calls <code>remove()</code> For example if the the call
     * <code>deleteLocalEntity(DefendantHome.class, new Integer(1));</code> was made then a
     * Defendant with primry key 1 would be deleted.
     * 
     * @param ejbLocalHomeClass the Class to find the JNDI name from
     * @param primarykey the Integer for the entity primary key.
     * @returns the EJBLocalObject for the for corresponding Entity. The returned object must be
     *          cast to the correct type.
     */
    void deleteLocalEntity(Class<?> ejbLocalHomeClass, Integer primarykey);

}
