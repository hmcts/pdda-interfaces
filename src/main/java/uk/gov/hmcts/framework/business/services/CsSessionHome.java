package uk.gov.hmcts.framework.business.services;

import jakarta.ejb.CreateException;
import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBObject;

import java.rmi.RemoteException;

public interface CsSessionHome extends EJBHome {

    EJBObject create() throws CreateException, RemoteException;

}
