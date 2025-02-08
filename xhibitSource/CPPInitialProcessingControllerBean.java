package uk.gov.courtservice.xhibit.business.services.cpp;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.ejb.FinderException;

import uk.gov.courtservice.framework.business.services.CSSessionBean;
import uk.gov.courtservice.framework.services.CSServices;
import uk.gov.courtservice.xhibit.business.entities.xhb_cpp_formatting.XhbCppFormattingBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_cpp_formatting.XhbCppFormattingBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_cpp_list.XhbCppListBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_cpp_list.XhbCppListBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_formatting.XhbFormattingBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_formatting.XhbFormattingBeanHelper2;
import uk.gov.courtservice.xhibit.business.services.cppstaginginbound.CppStagingInboundControllerException;
import uk.gov.courtservice.xhibit.business.services.cppstaginginbound.CppStagingInboundControllerLocal;
import uk.gov.courtservice.xhibit.business.services.cppstaginginbound.CppStagingInboundControllerLocalHome;
import uk.gov.courtservice.xhibit.business.services.cppformatting.CppFormattingHelper;
import uk.gov.courtservice.xhibit.business.services.cpplist.CppListControllerLocal;
import uk.gov.courtservice.xhibit.business.services.cpplist.CppListControllerLocalHome;
import uk.gov.courtservice.xhibit.business.services.cpplist.CppListHelper;
import uk.gov.courtservice.xhibit.business.services.cppstaginginbound.CppStagingInboundControllerBean.CppDocumentTypes;
import uk.gov.courtservice.xhibit.business.services.formatting.AbstractListXMLMergeUtils;
import uk.gov.courtservice.xhibit.business.services.formatting.AbstractXMLMergeUtils;
import uk.gov.courtservice.xhibit.business.services.formatting.AbstractXMLUtils;
import uk.gov.courtservice.xhibit.business.services.formatting.FormattingServices;
import uk.gov.courtservice.xhibit.business.services.validation.ValidationException;
import uk.gov.courtservice.xhibit.business.vos.entities.CppFormattingBasicValue;
import uk.gov.courtservice.xhibit.business.vos.entities.CppListBasicValue;
import uk.gov.courtservice.xhibit.business.vos.entities.CppStagingInboundBasicValue;


/**
 * 
 * Controller Bean For Starting Timers for dealing with documents that have been inserted into XHB_CPP_STAGING_INBOUND
 * sets teh status to IP (In Process) and validates the XML versus the XSD that it is valid.
 * 
 * @ejb.bean name="CPPInitialProcessingController" description="CPP Task
 *           Controller Bean" type="Stateless" view-type="both"
 *           jndi-name="CPPInitialProcessingControllerHome"
 *           local-jndi-name="CPPInitialProcessingControllerLocalHome"
 *           
 * @ejb.interface extends="uk.gov.courtservice.framework.scheduler.RemoteTask,javax.ejb.EJBObject"
 * 
 * @author satwell
 * @version $Id: CPPInitialProcessingControllerBean.java,v 1.0
 */
public class CPPInitialProcessingControllerBean  extends CSSessionBean implements SessionBean {

    private static final long serialVersionUID = 1L;
    private static final Logger log = CSServices.getLogger(CPPInitialProcessingControllerBean.class);
        
    Map<String, String> map = new HashMap<String, String>();
    
    private CppStagingInboundControllerLocal cppStagingInboundController;
    private CppListControllerLocal cppListController;
    private CppFormattingHelper cppfHelper = new CppFormattingHelper();
    
    private static String BATCH_USERNAME = "CPPX_SCHEDULED_JOB";
    
    private static AbstractXMLMergeUtils xmlUtils;
   
    /**
     * Initialises all of the instance variables for this session bean.
     * 
     * @see uk.gov.courtservice.framework.business.services.CSSessionBean
     *      #ejbCreate()
     */
    @Override
	public void ejbCreate() throws CreateException {
        super.ejbCreate();
        
        this.cppStagingInboundController = (CppStagingInboundControllerLocal) CSServices.getEJBServices().createLocalSession(
        		CppStagingInboundControllerLocalHome.class);
        
        this.cppListController = (CppListControllerLocal) CSServices.getEJBServices().createLocalSession(
        		CppListControllerLocalHome.class);
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer
     * process. This method must have the same transactional behaviour as
     * processFormattingDocument
     * 
     * @ejb.interface-method view-type="both"
     * @ejb.transaction type="Required"
	 * 
     */
    public void doTask(String taskName) {   	 
    	processCPPStagingInboundMessages();
    }
    
    /**
     * Implementation of RemoteTask so that this process is called by the timer
     * process.
     * 
     * This method will:
     * 1. Get all unprocessed records in the XHB_CPP_STAGING_INBOUND table
     * 2. Process each record as follows:
     * --- Set the status to IP (In Process) so that no other process picks it up
     * --- Validate the XML against the appropriate schema for the document type
     * --- Record the success or failure of the validation as appropriate
     * 
     * Appropriate logging will be done to provide feedback in logs where it would be useful 
     * 
     * @ejb.interface-method view-type="both"
     * @ejb.transaction type="Required"
     */
    public void processCPPStagingInboundMessages() {
    	String methodName = "processCPPStagingInboundMessages()";
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");
		
    	
    	handleNewDocuments();
    	handleStuckDocuments();
	}
    
    
    /**
     * Process any completely unprocessed (generally these will be new records unless manually amended status) documents that have appeared in XHB_CPP_STAGING_INBOUND 
     */
    @SuppressWarnings("unchecked")
	public void handleNewDocuments() {
    	String methodName = "handleNewDocuments()";
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");
    	
    	ArrayList<CppStagingInboundBasicValue>docs = null;
    	try {
	    	// Step 1: Get any unprocessed documents
    		docs = cppStagingInboundController.getLatestUnprocessedDocument();
    	} catch (EJBException e) {
    		log.error("EJB Exception when obtaining the next document received from CPP that has not been processed at all");
    		e.printStackTrace();
    	} catch (CppStagingInboundControllerException e) {
    		log.error("CPP Staging Inbound Controller Exception when obtaining the next document received from CPP that has not been processed at all");
			e.printStackTrace();
		}

    	if (docs != null) {
    		
    		for(int i=0;i<docs.size();i++) {
    			
    			try {
		    		if (log.isDebugEnabled())
							log.debug("Document to validate:: " +docs.get(i).toString());
		    		
		    		// Set the status to IN_PROCESS so that no other incoming process can pick up this document
			    	cppStagingInboundController.updateStatusInProcess(docs.get(i), BATCH_USERNAME);
		    		
		    		// Step 2: Validate a document which has the VALIDATION_STATUS='IP'
		    		// Commented out next line which was useful for unit testing but not necessary for the end to end process
					//thisDoc = cppStagingInboundController.getNextDocumentToValidate();
					
					// Performing a valdation will also check that the DOCUMENT_NAME and DOCUMENT_TYPE values are ok
					boolean docIsValid = cppStagingInboundController.validateDocument(docs.get(i), BATCH_USERNAME);
					
					if (docIsValid) {
						if (log.isDebugEnabled())
							log.debug("The document has been successfully validated");
						
						// Now attempt to process the validated document - i.e. examine XML and insert into downstream database tables
						if (processValidatedDocument(docs.get(i))) {
							if (log.isDebugEnabled())
								log.debug("The validated document has been successfully processed");
						} else {
							if (log.isDebugEnabled())
								log.debug("The validated document has failed to be processed. Check database error column for this record for further details.");
						}
					} else {
						// Logging a validation failure as an error at this time
						log.error("The document is not valid. Check XHB_CPP_STAGING_INBOUND.ERROR_MESSAGE for more details. Document details are: "+ docs.get(i).toString());
					}
					
				// Not throwing any exceptions here as a failure processing one document should not impact others
	    		} catch (ValidationException e) {
	    			log.error("Validation error when validating document. Turn debugging on for more info. Error: "+e.getMessage());
	    			e.printStackTrace();
	    		} catch (CppStagingInboundControllerException cppsie) {
	    			log.error("Error in EJB when validating document. Turn debugging on for more info. Error: "+cppsie.getMessage());
	    			cppsie.printStackTrace();
	    		} catch (EJBException ex) {
	                log.error("Error in EJB land during validation. Turn debugging on for more info.", ex);
	                ex.printStackTrace();
	    		} catch (Exception e) {
	    			log.error("Error validating document. Turn debugging on for more info. Error: "+e.getMessage());
	    			e.printStackTrace();
	    		}
    		}
    	} else {
    		// There are no documents currently to validate
    		if (log.isDebugEnabled())
    			log.debug("There are no unprocessed CPP documents at this time");
    	}

		 
    }
    
    
    /**
     * Check to see if there are any "stuck" records that have been validated but not moved on for processing and process
     */
    @SuppressWarnings("unchecked")
	public void handleStuckDocuments() {
    	String methodName = "handleStuckDocuments()";
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");
 
		ArrayList<CppStagingInboundBasicValue> docs = null;
    	try {
    		docs = cppStagingInboundController.getNextValidatedDocument();
    	} catch (CppStagingInboundControllerException cppsie) {
			log.error("Error in EJB when processing a document that has already been validated. Turn debugging on for more info. Error: "+cppsie.getMessage());
			cppsie.printStackTrace();
		} catch (EJBException ex) {
			log.error("Error in EJB land during validation. Turn debugging on for more info.", ex);
            ex.printStackTrace();          			
		}
    	
    	if (docs != null) {
    		for(int i=0;i<docs.size();i++) {
    			
    			try {
	    			if (docs.get(i) != null) {
	    				// Now attempt to process the validated document - i.e. examine XML and insert into downstream database tables
						if (processValidatedDocument(docs.get(i))) {
							if (log.isDebugEnabled())
								log.debug("The validated document has been successfully processed");
						} else {
							if (log.isDebugEnabled())
								log.debug("The validated document has failed to be processed. Check database error column for this record for further details.");
						}
			    	}
	    			
    			// Not throwing any exceptions here as a failure processing one document should not impact others
	    		} catch (CppStagingInboundControllerException cppsie) {
	    			log.error("Error in EJB when processing a document that has already been validated. Turn debugging on for more info. Error: "+cppsie.getMessage());
	    			cppsie.printStackTrace();
	    		} catch (EJBException ex) {
	    			log.error("Error in EJB land during validation. Turn debugging on for more info.", ex);
	                ex.printStackTrace();
	    		} catch (Exception e) {
	    			log.error("Error processing a document that has already been validated. Turn debugging on for more info. Error: "+e.getMessage());
	    			e.printStackTrace();
	    		}
	    	}
    	}
    	
    }
    
        	
	/**
	 * Get the earliest document that has been validated but not yet processed and attempt to process it.
	 * Processing means that based on the document type data is extracted from the XML and used to populate records in
	 * XHB_CPP_LIST or XHB_CPP_FORMATTING
	 * 
	 * If a process fails then the status needs to be updated accordingly so that it doesnt get picked up again until it
	 * has been fixed
	 * 
	 * @return
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public boolean processValidatedDocument(CppStagingInboundBasicValue thisDoc) throws Exception {
		String methodName = "processValidatedDocument() - thisDoc: "+thisDoc.toString();
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");

		boolean recordProcessing = true;
		String documentType = thisDoc.getDocumentType(); // Will be valid at this stage
		
		String clobXml = "";
		try {
			clobXml = cppStagingInboundController.getClobXmlAsString(thisDoc.getClobId());
		} catch (NullPointerException npe) {
			log.error("There was a problem obtaining the clob data: clob_id="+thisDoc.getClobId());
			return false;
		}
		
		try {
			// Which type of document is this?
			if (documentType.equals(CppDocumentTypes.DL+"") ||
					documentType.equals(CppDocumentTypes.FL+"") ||
					documentType.equals(CppDocumentTypes.WL+"")) {
	
				// If there is an existing list in XHB_CPP_LIST then the record will be updated
				// Otherwise a new record will be created;
				createUpdateListRecords(thisDoc, clobXml);			
				
			} else if (documentType.equals(CppDocumentTypes.PD+"") || documentType.equals(CppDocumentTypes.WP+"")) {
				
				createUpdateNonListRecords(thisDoc);
				
			} else {
				log.error("Not a valid document type");
				cppStagingInboundController.updateStatusProcessingFail(thisDoc, "Problem reconciling document type after successful validation", BATCH_USERNAME);
				return false;
			}
		} catch (EJBException e) {
			// If we get an error then we need to set record in XHB_STAGING_INBOUND to failed so that it doesnt keep getting attempted
			cppStagingInboundController.updateStatusProcessingFail(thisDoc, "Error processing document. See logs for details.", BATCH_USERNAME);
			
			this.errorHandling(methodName, e);
			throw e;
		}  catch (Exception e) {
			// If we get an error then we need to set record in XHB_STAGING_INBOUND to failed so that it doesnt keep getting attempted
			cppStagingInboundController.updateStatusProcessingFail(thisDoc, "Error processing document. See logs for details.", BATCH_USERNAME);
			
			this.errorHandling(methodName, e);
			throw e;
		}
		
		return recordProcessing;
	}
	
	
	/**
	 * 
	 * @param xml
	 * @param documentType
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException
	 * @return Date
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public Date getListStartDate(String xml, String documentType) throws ParserConfigurationException, IOException, SAXException {
		String methodName = "getListStartDate() - xml: "+xml+" , documentType: "+documentType;
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");

		Document xhibitDocument = null;
		Date listStartDate = null;
		
		xmlUtils = FormattingServices.getXMLUtils(documentType);
		try {
			xhibitDocument = AbstractXMLUtils.getDocBuilder().parse(new InputSource(new StringReader(xml)));
			listStartDate = ((AbstractListXMLMergeUtils) xmlUtils).getListStartDateFromDocument(xhibitDocument);
		} catch (SAXException e) {
			log.error("SAX Error whilst parsing XML to find list start date");
			this.errorHandling(methodName, e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error whilst parsing XML to find list start date");
			this.errorHandling(methodName, e);
			throw e;
		} catch (ParserConfigurationException e) {
			log.error("Parser Config Error whilst parsing XML to find list start date");
			this.errorHandling(methodName, e);
			throw e;
		}
		
		return listStartDate;
	}
	
	
	/**
	 * 
	 * @param xml
	 * @param documentType
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @return Date
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public Date getListEndDate(String xml, String documentType) throws ParserConfigurationException, IOException, SAXException {
		String methodName = "getListEndDate() - xml: "+xml+" , documentType: "+documentType;
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");

		Document xhibitDocument = null;
		Date listEndDate = null;
		
		xmlUtils = FormattingServices.getXMLUtils(documentType);
		try {
			xhibitDocument = AbstractXMLUtils.getDocBuilder().parse(new InputSource(new StringReader(xml)));
			listEndDate = ((AbstractListXMLMergeUtils) xmlUtils).getListEndDateFromDocument(xhibitDocument);
		} catch (SAXException e) {
			log.error("SAX Error whilst parsing XML to find list end date");
			this.errorHandling(methodName, e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error whilst parsing XML to find list end date");
			this.errorHandling(methodName, e);
			throw e;
		} catch (ParserConfigurationException e) {
			log.error("Parser Config Error whilst parsing XML to find list end date");
			this.errorHandling(methodName, e);
			throw e;
		}
		
		return listEndDate;
	}
	
	/**
	 * 
	 * @param xml
	 * @param documentType
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @return
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public String getCourtHouseCode(String xml, String documentType) throws ParserConfigurationException, IOException, SAXException {
		String methodName = "getCourtHouseCode() - xml: "+xml+" , documentType: "+documentType;
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");

		Document xhibitDocument = null;
		String courtCode = null;
		
		xmlUtils = FormattingServices.getXMLUtils(documentType);
		try {
			xhibitDocument = AbstractXMLUtils.getDocBuilder().parse(new InputSource(new StringReader(xml)));
			courtCode = ((AbstractListXMLMergeUtils) xmlUtils).getCourtHouseCodeFromDocument(xhibitDocument);
		} catch (SAXException e) {
			log.error("SAX Error whilst parsing XML to find court code");
			this.errorHandling(methodName, e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error whilst parsing XML to find court code");
			this.errorHandling(methodName, e);
			throw e;
		} catch (ParserConfigurationException e) {
			log.error("Parser Config Error whilst parsing XML to find court code");
			this.errorHandling(methodName, e);
			throw e;
		}
		
		return courtCode;
	}
	
	
	/**
	 * 
	 * @param thisDoc
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public void createUpdateNonListRecords(CppStagingInboundBasicValue thisDoc) {
		String methodName = "createUpdateNonListRecords() - thisDoc: "+thisDoc.toString();
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");

		
		try {
			int courtId = cppStagingInboundController.getCourtId(thisDoc.getCourtCode());
			

			// If court id is not > 0 then the court could not be found and processing of this record should stop
			if (courtId > 0) {
				String documentType = thisDoc.getDocumentType();
				if(documentType.equalsIgnoreCase("WP")){
					documentType="IWP";
				}
				CppFormattingBasicValue docToUpdate = null;
				try {
					docToUpdate = cppfHelper.findLatestByCourtDateInDoc(courtId, documentType);
				} catch (FinderException e) {
					log.error("Cannot find data in XHB_CPP_FORMATTING due to a finder error.");
					e.printStackTrace();
				}
				
				
				if (docToUpdate != null) { // Update - directly rather through maintainer as that was generating CMP/CMR relationship errors
					docToUpdate.setFormatStatus(CppFormattingHelper.FORMAT_STATUS_NOT_PROCESSED);
					docToUpdate.setXmlDocumentClobId(thisDoc.getClobId());
					docToUpdate.setDateIn(thisDoc.getTimeLoaded());
					docToUpdate.setStagingTableId(thisDoc.getCppStagingInboundId());
					
					cppfHelper.updateCppFormatting(docToUpdate, BATCH_USERNAME);
	
					//XhbCppFormatting updated = XhbCppFormattingBeanHelper2.updateLocal(docToUpdate2);
					
				} else { // Insert
					// Create a record to insert
					XhbCppFormattingBasicValue docToCreate = new XhbCppFormattingBasicValue();
					docToCreate.setCourtId(courtId);
					docToCreate.setFormatStatus(CppFormattingHelper.FORMAT_STATUS_NOT_PROCESSED);					
					docToCreate.setDateIn(thisDoc.getTimeLoaded());
					docToCreate.setDocumentType(documentType);
					docToCreate.setCppStagingTableId(thisDoc.getCppStagingInboundId());
					docToCreate.setXmlDocumentClobId(thisDoc.getClobId());
					docToCreate.setObsInd("N");
					
					XhbCppFormattingBeanHelper2.create(docToCreate);
				}
				
				if(documentType.equalsIgnoreCase("IWP")){
					// Also need to add 2 new records to XHB_FORMATTING
					XhbFormattingBasicValue xfbv = new XhbFormattingBasicValue();
					xfbv.setCourtId(courtId);
					xfbv.setDateIn(thisDoc.getTimeLoaded());
					xfbv.setDistributionType("FTP");
					xfbv.setDocumentType(documentType);
					xfbv.setFormatStatus(CppFormattingHelper.FORMAT_STATUS_NOT_PROCESSED);
					xfbv.setMimeType("HTM");
					xfbv.setXmlDocumentClobId(new Long(0));
					xfbv.setCountry("GB");
					
					// Create the "en" version
					xfbv.setLanguage("en");
					XhbFormattingBeanHelper2.create(xfbv);
					
					// Create the "cy" version
					xfbv.setLanguage("cy");
					XhbFormattingBeanHelper2.create(xfbv);
				}				
				
				// If all successful then we need to set record in XHB_STAGING_INBOUND to indicate this
				cppStagingInboundController.updateStatusProcessingSuccess(thisDoc, BATCH_USERNAME);
			} else {
				// Court id is invalid
				cppStagingInboundController.updateStatusProcessingFail(thisDoc, "Court id was invalid", BATCH_USERNAME);
			}
			

		} catch (EJBException e) {
			this.errorHandling(methodName, e);
			throw e;
		}
		
	}
	
	
	/**
	 * 
	 * @param thisDoc
	 * 
	 * @ejb.interface-method view-type="both"
	 */
	public void createUpdateListRecords(CppStagingInboundBasicValue thisDoc, String clobXml) throws Exception {
		String methodName = "createUpdateListRecords() - thisDoc: "+thisDoc.toString();
		if (log.isDebugEnabled())
			log.debug(methodName + " : entered");
		
		try {
			String documentType = thisDoc.getDocumentType();
			Date listStartDate = getListStartDate(clobXml, documentType);
			Date listEndDate = getListEndDate(clobXml, documentType);
			CppListBasicValue docToUpdate = cppListController.checkForExistingCppListRecord(thisDoc.getCourtCode().intValue(), documentType, listStartDate, listEndDate);
			if (docToUpdate != null) { // Update
				// Set updated values
				docToUpdate.setStatus(CppListHelper.NOT_PROCESSED);
				docToUpdate.setTimeLoaded(thisDoc.getTimeLoaded());
				docToUpdate.setListStartDate(listStartDate);
				docToUpdate.setListEndDate(listEndDate);
				docToUpdate.setListClobId(thisDoc.getClobId());
				docToUpdate.setMergedClobId(thisDoc.getClobId());

				try {
					//XhbCppListBeanHelper2.update(docToUpdate);
					cppListController.updateCppList(docToUpdate, BATCH_USERNAME);
				} catch (FinderException e) {
					log.error("Cannot update XHB_CPP_LIST due to a finder error.");
					e.printStackTrace();
				}
			} else { // Insert
				// Create a record to insert
				XhbCppListBasicValue docToCreate = new XhbCppListBasicValue();
				docToCreate.setCourtCode(thisDoc.getCourtCode().intValue());
				docToCreate.setStatus(CppListHelper.NOT_PROCESSED);
				docToCreate.setTimeLoaded(thisDoc.getTimeLoaded());
				docToCreate.setListType(documentType.substring(0,1)); // Only want first letter
				docToCreate.setListStartDate(listStartDate);
				docToCreate.setListEndDate(listEndDate);
				docToCreate.setListClobId(thisDoc.getClobId());
				docToCreate.setObsInd("N");
				
				XhbCppListBeanHelper2.create(docToCreate);
			}
			
			// If all successful then we need to set record in XHB_STAGING_INBOUND to indicate this
			cppStagingInboundController.updateStatusProcessingSuccess(thisDoc, BATCH_USERNAME);
			
		} catch (EJBException e) {
			this.errorHandling(methodName, e);
			throw e;
		}  catch (Exception e) {
			this.errorHandling(methodName, e);
			throw e;
		}
		
	}
	
	/**
	 * Errorhandling method that will be used for all catch blocks where the
	 * ???? is being caught. This is used since
	 * all public methods in this class are handled in the same way.
	 * 
	 * @param methodName
	 *            String
	 * @param e
	 *            Exception
	 */
	private void errorHandling(String methodName, Exception e) {
		e.printStackTrace();
		ctx.setRollbackOnly();
		CSServices.getDefaultErrorHandler().handleError(e, getClass());
		log.error(methodName + " : failed! Transaction Rollback");
	}

}