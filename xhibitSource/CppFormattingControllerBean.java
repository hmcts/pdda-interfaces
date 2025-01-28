package uk.gov.courtservice.xhibit.business.services.cppformatting;

import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;

import org.apache.log4j.Logger;

import uk.gov.courtservice.framework.business.services.CSSessionBean;
import uk.gov.courtservice.framework.services.CSServices;
import uk.gov.courtservice.xhibit.business.vos.entities.CppFormattingBasicValue;

/**
 * <p>
 * Title: CppFormattingControllerBean
 * </p>
 * <p>
 * Description: Local interface to CPP Formatting session facade.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2019
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @ejb.bean name="CppFormattingController" description="CPP Formatting
 *           Session Bean" type="Stateless" view-type="both"
 *           jndi-name="CppFormattingControllerHome"
 *           local-jndi-name="CppFormattingControllerLocalHome"
 * @ejb.interface extends="uk.gov.courtservice.framework.scheduler.RemoteTask,javax.ejb.EJBObject"
 * 
 * @author Chris Vincent
 * @version $Revision: 1.0 $
 */
public class CppFormattingControllerBean extends CSSessionBean implements SessionBean {

	private static final long serialVersionUID = -1482124779093244736L;
	private static final Logger log = CSServices.getLogger(CppFormattingControllerBean.class);

	// set up the helper class
	CppFormattingHelper cppFormattingHelper = new CppFormattingHelper();
	
	/**
     * Initialises all of the instance variables for this session bean.
     * 
     * @see uk.gov.courtservice.framework.business.services.CSSessionBean
     *      #ejbCreate()
     */
    public void ejbCreate() throws CreateException {
        super.ejbCreate(); 
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer
     * process.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 * 
     */
    public void doTask(String taskName) {   	 
    	processCPPPublicDisplayDocs();
    }
    
    /**
     * Implementation of RemoteTask so that this process is called by the timer
     * process.
     * @throws CppFormattingControllerException 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
   public void processCPPPublicDisplayDocs() {
	   String methodName = "processCPPPublicDisplayDocs() - ";
	   // Get a list of CPP_FORMATTING objects that have a type of 'PD', a format status of 'ND' and
	   // a date in of today
	   try {
		   ArrayList<CppFormattingBasicValue> cppList = cppFormattingHelper.findAllNewPublicDisplayDocs();
		   for ( CppFormattingBasicValue cF : cppList ) {
			   // For each CPP_FORMATTING object, extract the court Id, use it to refresh all pages for that court
			   cppFormattingHelper.refreshPublicDisplaysForCourt(cF.getCourtId(), null);
			   
			   // Update the format status to 'MS'
			   updateStatusSuccess(cF);
		   }
	   } 
	   catch (CppFormattingControllerException cfce) {
			this.errorHandling(methodName, cfce);
		}
   }

	/**
	 * <p>
	 * Returns the latest unprocessed XHB_CPP_FORMATTING record for Public Display
	 * </p>
	 * 
	 * @param courtId
	 *            ID of the court
	 * @return CppFormatting
	 * @throws CppFormattingControllerException
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public CppFormattingBasicValue getLatestPublicDisplayDocument(Integer courtId)
			throws CppFormattingControllerException {
		String methodName = "getLatestPublicDisplayDocument(" + courtId + ") - ";
		log.debug(methodName + " : entered");
		
		CppFormattingBasicValue doc = null;
		try {
			doc = cppFormattingHelper.getLatestPublicDisplayDocument(courtId);
		} catch (CppFormattingControllerException cfce) {
			this.errorHandling(methodName, cfce);
			throw cfce;
		}
		return doc;
	}
	
	/**
	 * <p>
	 * Returns the latest unprocessed XHB_CPP_FORMATTING record for Internet Web Pages
	 * </p>
	 * 
	 * @param courtId
	 *            ID of the court
	 * @return CppFormattingBasicValue
	 * @throws CppFormattingControllerException
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public CppFormattingBasicValue getLatestWebPageDocument(Integer courtId)
			throws CppFormattingControllerException {
		String methodName = "getLatestWebPageDocument(" + courtId + ") - ";
		log.debug(methodName + " : entered");
		CppFormattingBasicValue doc = null;

		try {
			doc = cppFormattingHelper.getLatestWebPageDocument(courtId);
		} catch (CppFormattingControllerException cfce) {
			this.errorHandling(methodName, cfce);
			throw cfce;
		}
		return doc;
	}
	
	/**
	 * Updates an XHB_CPP_FORMATTING record with a status of successfully merged/processed 
	 * 
	 * @param CppFormattingBasicValue cppFormattingBasicValue
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public void updateStatusSuccess(CppFormattingBasicValue cppFormattingBasicValue) {
		String methodName = "updateStatusSuccess(" + cppFormattingBasicValue + ") - ";
		log.debug(methodName + " : entered");
		try {
			cppFormattingHelper.updateCppFormattingStatus(
					cppFormattingBasicValue, 
					CppFormattingHelper.FORMAT_STATUS_SUCCESS
			);
				
		} catch (EJBException e) {
			this.errorHandling(methodName, e);
			throw e;
		}
	}
	
	/**
	 * Updates an XHB_CPP_FORMATTING record with a status of merge failed 
	 * 
	 * @param CppFormattingBasicValue cppFormattingBasicValue
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public void updateStatusFailed(CppFormattingBasicValue cppFormattingBasicValue) {
		String methodName = "updateStatusFailed(" + cppFormattingBasicValue + ") - ";
		log.debug(methodName + " : entered");
		try {
			cppFormattingHelper.updateCppFormattingStatus(
					cppFormattingBasicValue, 
					CppFormattingHelper.FORMAT_STATUS_FAIL
			);
				
		} catch (EJBException e) {
			this.errorHandling(methodName, e);
			throw e;
		}
	}

	/**
	 * Errorhandling method that will be used for all catch blocks where the
	 * CounselFacilitiesControllerException is being caught. This is used since
	 * all public methods in this class are handled in the same way.
	 * 
	 * @param methodName
	 *            String
	 * @param e
	 *            Exception
	 */
	private void errorHandling(String methodName, Exception e) {
		ctx.setRollbackOnly();
		CSServices.getDefaultErrorHandler().handleError(e, getClass());
		log.debug(methodName + " : failed! Transaction Rollback");
	}


}