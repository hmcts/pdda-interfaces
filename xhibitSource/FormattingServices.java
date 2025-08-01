package uk.gov.courtservice.xhibit.business.services.formatting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

//import com.bea.xquery.xdbc.Connection;
import java.sql.Connection;
//import com.bea.xquery.xdbc.PreparedStatement;
import java.sql.PreparedStatement;

import uk.gov.courtservice.framework.jdbc.core.JdbcHelper;
import uk.gov.courtservice.framework.services.CSServices;
import uk.gov.courtservice.framework.xml.sax.ForkContentHandler;
import uk.gov.courtservice.xhibit.business.database.formatting.FormatingDatabaseProcedures;
import uk.gov.courtservice.xhibit.business.entities.xhb_clob.XhbClobBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_clob.XhbClobBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_config_prop.XhbConfigPropBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_court.XhbCourt;
import uk.gov.courtservice.xhibit.business.entities.xhb_court.XhbCourtBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_court_site.XhbCourtSiteBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_court_site.XhbCourtSiteBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_document_control.XhbDocumentControlBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_document_control.XhbDocumentControlBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_document_recipient.XhbDocumentRecipientBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_document_recipient.XhbDocumentRecipientBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument;
import uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocumentBeanHelper2;
import uk.gov.courtservice.xhibit.business.entities.xhb_formatting.XhbFormattingBasicValue;
import uk.gov.courtservice.xhibit.business.entities.xhb_formatting.XhbFormattingBeanHelper2;
import uk.gov.courtservice.xhibit.business.exception.formatting.FormattingException;
import uk.gov.courtservice.xhibit.business.services.cppformatting.CppFormattingHelper;
import uk.gov.courtservice.xhibit.business.services.cpplist.CppListHelper;
import uk.gov.courtservice.xhibit.business.services.translation.TranslationBundlesCache;
import uk.gov.courtservice.xhibit.business.vos.entities.CppFormattingBasicValue;
import uk.gov.courtservice.xhibit.business.vos.entities.CppFormattingMergeBasicValue;
import uk.gov.courtservice.xhibit.business.vos.entities.CppListBasicValue;
import uk.gov.courtservice.xhibit.business.vos.entities.CppListComplexValue;
import uk.gov.courtservice.xhibit.business.vos.formatting.FormattingValue;
import uk.gov.courtservice.xhibit.business.xmlbinding.formatting.FormattingConfig;


/**
 * <p>
 * Title: Formatting of xml using passed in parameters consisting of a number of
 * xsls.
 * </p>
 * <p>
 * Description: An XML pipeline is created to transform the inputted xml into
 * formatted xml/pdf depending on the MimeType parameter.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bal Bhamra
 * @version $Id: FormattingServices.java,v 1.14 2014/06/20 16:49:28 atwells Exp $
 */

public class FormattingServices {
    // Logging
    private static final Logger log = CSServices.getLogger(FormattingServices.class);
    private static final String IWP = "IWP";
    private static final String LISTS = "LISTS";
    private static AbstractXMLMergeUtils xmlUtils;
    private FormatingDatabaseProcedures formattingDatabaseProcedures;  
    
    /**
     * FOP stuff
     */
    private static final org.apache.fop.apps.FopFactory fopFactory = getFopFactory(new File(".").toURI());
    private static FOUserAgent foUserAgent;

    // The fop logger adapter
    private static final Log4JLogger fopLog = new Log4JLogger(log);
    static {
        // set as default for screen logger for fop
        
    	// MessageHandler.setScreenLogger(fopLog);
    }

    // Date Format for java date
    private static final SimpleDateFormat javaDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TRANSFORMATION_ERROR = " An error has occured during the transformation process";
    private static final String PARSER_ERROR = " An error has occured during the parser configuration";
    private static final String MERGING_ERROR = "An error occurred during merging xhibit document ";
    private static final String MERGING_EXCEPTION_STRING = "Error Merging: ";
    private static final String WL_SCHEMA = "CPPX_SchemaWL";
	private static final String FL_SCHEMA = "CPPX_SchemaFL";
	private static final String DL_SCHEMA = "CPPX_SchemaDL";
    private static final String INITIAL_WL_SCHEMA = getWarnedListVersion();
	private static final String INITIAL_FL_SCHEMA = getFirmListVersion();
	private static final String INITIAL_DL_SCHEMA = getDailyListVersion();

    private CppFormattingHelper helper = new CppFormattingHelper();
    private CppListHelper cppListHelper = new CppListHelper();
    /**
     * This method is responsible for transforming the information from the
     * reader using xslts dependant on the FormattingParameters and writes the
     * result to Output stream.
     * 
     * @param formattingValue
     * @throws FormattingException
     */
    public void processDocument(FormattingValue formattingValue) {
        processDocument(formattingValue, getTranslationXml());
    }
    
    private static String getWarnedListVersion() {
    	String version = getListVersions("('WL')");
    	return version;
    }
    
    private static String getFirmListVersion() {
    	String version = getListVersions("('FL')");
    	return version;
    }
    
    private static String getDailyListVersion() {
    	String version = getListVersions("('DL')");
    	return version;
    }
    
    /**
     * We want to get the version numbers of the xsd's to use from the XHIBIT.CJI_DOCUMENT_TYPE table
     * Note: NOT the CJIT.CJI_DOCUMENT_TYPE table
     * 
     * @param type
     * @return
     */
    private static String getListVersions(String type) {
    	log.debug("BEGIN:: looking for version number from CJI_DOCUMENT for "+type);
    	Connection conn = null;
    	PreparedStatement statement = null;
    	ResultSet result = null;
    	final String query = "select schema_name from cji_document_type where internal_code in " + type;
    	
    	try{
    		conn = CSServices.getServiceLocator().getDataSource().getConnection();
 			statement = conn.prepareStatement(query);
 			//statement.setString(1, val); 
 			result = statement.executeQuery();
 			String verString = null;
 			result.next();
 			verString =  result.getString(1); 			
 			String splitStr[] = verString.split(" ");
 			verString = splitStr[1];
 			log.debug("Setting version number for "+type+" : "+verString);
            return verString;
    	} catch (SQLException e) {
 			log.error("An error occurred while retrieving the LIST VERSION TEXT from the database "+e);
 			return null;
 		} finally {
 			JdbcHelper.closeStatement(statement);
 	        JdbcHelper.closeConnection(conn);
 		}
    }

    private String getTranslationXml() {
        return TranslationBundlesCache.getInstance().getTranslationBundles().toXml();
    }

    private void processDocument(FormattingValue formattingValue, String translationXml) throws FormattingException {
        try {
        	// Get the xmlUtils type
	    	xmlUtils = getXMLUtils(formattingValue.getDocumentType());
	    	
            if (log.isDebugEnabled() || xmlUtils != null) {
            	log.debug("processDocument(" + formattingValue + ")");
                
            	//check the xml_document_clob_id if not 0 then we know it's CPP
            	if(formattingValue.getXmlDocumentClobId()!= null && formattingValue.getXmlDocumentClobId()!=0) {
            		log.debug("formattingValue is an Xhibit document and ID is "+formattingValue.getFormattingId());
            		try {
            			String cppClobAsString = null;
            			CppListComplexValue cppList = null;
            			CppFormattingBasicValue val =  null;
            			Document xhibitDocument = null;
            	    	
            	    	// Get the clob to merge (if required)
            			if (xmlUtils != null) {
            				String xhibitDocClob = XhbClobBeanHelper2.findByPrimaryKey(formattingValue.getXmlDocumentClobId()).getClobData();
							xhibitDocument = AbstractXMLUtils.getDocBuilder().parse(new InputSource(new StringReader(xhibitDocClob)));
	        	    		if (LISTS.equals(xmlUtils.getMergeType()) && isCPPCourt(formattingValue.getCourtId())) {
	        	    			String listType = formattingValue.getDocumentType().substring(0, 1);
	            	    		Date listStartDate = ((AbstractListXMLMergeUtils) xmlUtils).getListStartDateFromDocument(xhibitDocument);   		
	            	    		cppList = cppListHelper.getLatestCPPList(formattingValue.getCourtId(), listType, listStartDate);
	            	    		if (cppList != null && cppList.getListClob() != null) {
	            	    			cppClobAsString = cppList.getListClob().getClobData();
	            	    			// Use the unmerged document
	            	    			xhibitDocument = AbstractXMLUtils.getDocBuilder().parse(new InputSource(formattingValue.getReader()));
	            	    			
	            	    		   	// Clear the previous merge attempts and set in progress
	            	    	    	cppList.setMergedClobId(null);
	            	    	    	cppList.setMergedClob(null);
	            	    	    	cppList.setErrorMessage(null);
	            	    	    	cppList.setStatus(CppListBasicValue.Status.IN_PROGRESS); 
	            	    			updateCPPList(cppList);
	            	    		}
	        	    		} else if (IWP.equals(xmlUtils.getMergeType()) && isCPPCourt(formattingValue.getCourtId()) && isMergeAllowed()) {
	        	    			val = helper.findLatestByCourtDateInDoc(formattingValue.getCourtId(),IWP);
	                			if(val!=null) {
	                				cppClobAsString = XhbClobBeanHelper2.findByPrimaryKey(val.getXmlDocumentClobId()).getClobData();
	                			}		
	        	    		}
            			}
            			
	            		if (cppClobAsString != null) {
	            			Document cppDocument = AbstractXMLUtils.getDocBuilder().parse(new InputSource(new StringReader(cppClobAsString)));
	            			if("WL".equalsIgnoreCase(formattingValue.getDocumentType())||"WLL".equalsIgnoreCase(formattingValue.getDocumentType())) {
	            				try {
	            					cppDocument = replaceCourtSiteWithListName(cppDocument,formattingValue.getCourtId());
	            				} catch (Exception ex) {
	                               //update the cpp list and just carry on using the xhibit list       
	            					if(ex.getCause() instanceof ObjectNotFoundException) {
		            					triggerMergingError(val, ex, cppList, "A courtsite in CPP xml doesn't exist");
	            					} else {
		            					triggerMergingError(val, ex, cppList, null);

	            					}
	            					
	            					//have to re set this or else we get an error 
	            					StringWriter write = new StringWriter();
	            					TransformerFactory transF = TransformerFactory.newInstance();
	            					Transformer trans = transF.newTransformer();
	            					trans.transform(new DOMSource(xhibitDocument), new StreamResult(write));
	            					
	            					formattingValue.setReader(new StringReader(write.getBuffer().toString()));
	            					
	            					StringWriter buffer = new StringWriter();
	            			        transform(formattingValue, translationXml, buffer);
	                                log.debug("CPP document was not valid so processDocument(" + formattingValue + "):\n " + buffer.toString());
	                                //if it's a list 
	                                if(isCourtelSendableDoc(formattingValue.getDocumentType()) ){
	                	    			writeToCourtel(null, formattingValue.getXmlDocumentClobId());

	                                }
	                                //set back to null so that it doesn't try and merge below.
	                                cppDocument = null;
	        					}
	            			}
	            			try {
	            				//have to put this step in or else it'll try doing this after it's already written for a failed cpp.
	            				if(cppDocument!=null) {
	            					//if firm list we want to ensure it is in the correct order
	            					if("FL".equalsIgnoreCase(formattingValue.getDocumentType())||"FLL".equalsIgnoreCase(formattingValue.getDocumentType())) {
	            						cppDocument = ((FirmListXMLMergeUtils) xmlUtils).sortFirmCourtLists(cppDocument);
	            					}
	            					mergeDocuments(xhibitDocument, cppDocument, formattingValue, translationXml, val, cppList);
	            				}
            				} catch(FinderException ex) {
            			        throw new FormattingException(triggerMergingError(val, ex, cppList, null), ex);

            				} catch(IOException ex) {
            			        throw new FormattingException(triggerMergingError(val, ex, cppList, null), ex);

                    		} catch(XPathExpressionException ex) {
                    	        throw new FormattingException(triggerMergingError(val, ex, cppList, null), ex);
            				}

            			} else {
            				log.debug("No CPP documents to process");
            				StringWriter buffer = new StringWriter();
                            transform(formattingValue, translationXml, buffer);
                            log.debug("\n\n\n\n\n\n\nprocessDocument(" + formattingValue + "):\n " + buffer.toString());
                            //if it's a list 
                            if(isCourtelSendableDoc(formattingValue.getDocumentType()) ){
            	    			writeToCourtel(null, formattingValue.getXmlDocumentClobId());

            			}
            		}
            			  
            			//We should not get a finder exception as all the values we are searching on should not come back with a finder exception                          
            		} catch(FinderException ex) {
                        throw new FormattingException(TRANSFORMATION_ERROR, ex);            			
            		} catch (EJBException ex) {
                        throw new FormattingException(TRANSFORMATION_ERROR, ex);            			
            		} 
            	} else if (isListLetterDocType(formattingValue.getDocumentType())) { // Used for Distribute List letters
            		transform(formattingValue, translationXml, null);
            	} else if(isMergeAllowed()) {
            		log.debug("formattingValue is a CPP document and ID is "+formattingValue.getFormattingId());
        			CppFormattingBasicValue val = helper.findLatestByCourtDateInDoc(formattingValue.getCourtId(),IWP);
            		try {
            	        formattingDatabaseProcedures = new FormatingDatabaseProcedures(this);

            			String cppClobAsString = XhbClobBeanHelper2.findByPrimaryKey(val.getXmlDocumentClobId()).getClobData();
            			Document cppDocument = getDocBuilderWithNoExternals(cppClobAsString);
            	        List<Node> cppNodes = getCourtSites(cppDocument);
            	        ArrayList<StringBuffer> xhibitClobAsString = new ArrayList<StringBuffer>();
            	        for(int i=0;i<cppNodes.size();i++) {
            	        	String courtSite = cppNodes.get(i).getNodeValue().replaceAll("\n", "").replace("\r", "");

            	        	if (xhibitClobAsString==null || xhibitClobAsString.isEmpty()) {
            	        		Long xhibitClobId = formattingDatabaseProcedures.getLatestXhibitClobId(val.getCourtId(), IWP,formattingValue.getLocale().getLanguage(), courtSite);
            	        		if(xhibitClobId !=null) {
            	        			xhibitClobAsString.add(processIWPCPPFormatting(xhibitClobId, formattingValue, cppDocument, translationXml, val));
            	        			saveDocInfo(formattingValue, courtSite);
            	        		}
            	        	} else {
	            	        	boolean found = false;
	            	        	for(int i2=0;i2<xhibitClobAsString.size();i2++) {
	            	        		if(xhibitClobAsString.get(i2).indexOf("<courtsitename>"+courtSite)>=0){
	            	        			found = true;
	            	        			break;
	            	        		}
	            	        	}
	            	        	if(!found) {
	            	        		//we have to go and get the next xhibit document with this courtsite in it
	            	        		Long xhibitClobId = formattingDatabaseProcedures.getLatestXhibitClobId(val.getCourtId(), IWP,formattingValue.getLocale().getLanguage(), courtSite);
	            	        		//now we need to get the latest xhibit document 
	            	        		if(xhibitClobId!=null) {
	            	        		     XhbFormattingBasicValue bvCpp = XhbFormattingBeanHelper2.findByPrimaryKeyValue(formattingValue.getFormattingId());

	            	         			//create a new xhb_formatting row first -- as this is a new merged xml that needs to be processed 
	            	         			//create a blank row so that we can have this as the formatting row
	            	         			
	            	         			XhbFormattingBasicValue xhbFormatting = new XhbFormattingBasicValue(0,
	            	         					bvCpp.getDateIn(), 
	            	         					"DR",
	            	         					bvCpp.getDistributionType(),
	            	         					bvCpp.getMimeType(),
	            	         					bvCpp.getDocumentType(),
	            	         					bvCpp.getLastUpdateDate(),
	            	         					bvCpp.getCreationDate(),
	            	         					bvCpp.getCreatedBy(),
	            	         					bvCpp.getLastUpdatedBy(),
	            	         					bvCpp.getVersion(),
	            	         					bvCpp.getCourtId(), 
	            	         					null,
	            	         					new Long(0),bvCpp.getLanguage(),bvCpp.getCountry(),bvCpp.getMajorSchemaVersion(),bvCpp.getMinorSchemaVersion());

	            	         			XhbFormattingBasicValue createdBv = XhbFormattingBeanHelper2.create(xhbFormatting);
	            	         			FormattingValue cppFormattingVal = formattingDatabaseProcedures.getNewFormattingRow(createdBv.getFormattingId());
	                	        		xhibitClobAsString.add(processIWPCPPFormatting(xhibitClobId, cppFormattingVal, cppDocument, translationXml, val));
	            	        			saveDocInfo(cppFormattingVal, courtSite);
	            	        		}		            	       
	            	        	}
	            	        }
            	        }
            	        //if we haven't had anything to merge in then we want to fail it as otherwise we'll be creating a blank row
            	        if(xhibitClobAsString.isEmpty()) {
            	        	throw new FormattingException("No CPP data was merged in for formatting id of "+formattingValue.getFormattingId());
            	        }

            			//We should not get a finder exception as all the values we are searching on should not come back with a finder exception                          
            		} catch (XPathExpressionException ex) {
            			log.error(MERGING_ERROR+val.getCppFormattingId()+":"+ex.getMessage());
    					val.setErrorMessage(MERGING_EXCEPTION_STRING+val.getCppFormattingId()+" : "+ex.getMessage());
                        throw new FormattingException(MERGING_EXCEPTION_STRING+val.getCppFormattingId()+" : "+ex.getMessage(), ex);  
					} 
            	} else {
            		log.error("CPP document not merged in as too late in the day/passed allowed merge time");
            		//it's a cpp document but it's passed the 'allowed' merge time so write a failure to the row
    	        	throw new FormattingException("No CPP data was merged in for formatting id of "+formattingValue.getFormattingId()+" as too late in the day/passed allowed merge time");
            	}
            } else {
                transform(formattingValue, translationXml, null);
            }
        } catch (TransformerException e) {
            throw new FormattingException(TRANSFORMATION_ERROR, e);
        } catch (ParserConfigurationException e) {
            throw new FormattingException(PARSER_ERROR, e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new FormattingException(PARSER_ERROR, e);
        } catch (SAXException e) {
            throw new FormattingException(" A SAX error has occured", e);
        } catch (IOException e) {
            throw new FormattingException(" An IO Error has occured", e);
        } catch (FinderException e) {
            throw new FormattingException(" An Finder Error has occured", e);
		}
    }

    /**
     * Trigger a merging error
     * @param val
     * @param ex
     * @param cppList
     */
    private String triggerMergingError(CppFormattingBasicValue val, Exception ex, CppListComplexValue cppList, String errorMessage) {
    	String errorMsg = null;
		if (val != null) {
			log.error(MERGING_ERROR+val.getCppFormattingId()+":"+ex.getMessage());
			errorMsg = MERGING_EXCEPTION_STRING+val.getCppFormattingId()+" : "+ex.getMessage();
			val.setErrorMessage(errorMsg);
		}
		if (cppList != null) {
			if(errorMessage!=null) {
				cppList.setErrorMessage(errorMessage);
			} else {
				cppList.setErrorMessage(ex.getMessage());
			}
			cppList.setStatus(CppListBasicValue.Status.MERGE_FAILED);
			updateCPPList(cppList);
		}
		return errorMsg;
		
	}

	private void mergeDocuments(Document xhibitDocument, Document cppDocument, FormattingValue formattingValue, String translationXml, CppFormattingBasicValue val, CppListComplexValue cppList) 
    		throws XPathExpressionException, IOException, TransformerException, SAXException, ParserConfigurationException, ObjectNotFoundException {
    	//Do the merge
		Document mergedDocument = xmlUtils.merge(xhibitDocument,cppDocument);
		TransformerFactory transF = TransformerFactory.newInstance();
		Transformer trans = transF.newTransformer();
		StringWriter write = new StringWriter();
		trans.transform(new DOMSource(mergedDocument), new StreamResult(write));
		String mergedDoc = write.getBuffer().toString();
		
		
		try {
			
			//if something has gone wrong retrieving the WL file from the db then we don't want to 
			//stop processing completely, continue but with v 5.9
			String configProp = null;
			if(xmlUtils instanceof WarnedListXMLMergeUtils) {
				configProp = XhbConfigPropBeanHelper2.findByPropertyNameValue(WL_SCHEMA)[0].getPropertyValue();
				mergedDoc = mergedDoc.replace(INITIAL_WL_SCHEMA, configProp);
			} else if (xmlUtils instanceof FirmListXMLMergeUtils) {
				configProp = XhbConfigPropBeanHelper2.findByPropertyNameValue(FL_SCHEMA)[0].getPropertyValue();
				mergedDoc = mergedDoc.replace(INITIAL_FL_SCHEMA, configProp);
			} else if(xmlUtils instanceof DailyListXMLMergeUtils) {
				configProp = XhbConfigPropBeanHelper2.findByPropertyNameValue(DL_SCHEMA)[0].getPropertyValue();
				mergedDoc = mergedDoc.replace(INITIAL_DL_SCHEMA, configProp);
			}
			
		} catch(Exception e) {
	        log.error("Could not swap out versions of schema");

		}
		
	
		formattingValue.setReader(new StringReader(mergedDoc));	
		StringWriter buffer = new StringWriter();
        transform(formattingValue, translationXml, buffer);
        log.debug("\n\n\n\n\n\n\nprocessDocument(" + formattingValue + "):\n " + buffer.toString());
          
        //insert a row into xhb clob , update clob id and write a row in the formatting merge
        if (val != null) {
            CppFormattingMergeBasicValue baseVal = new CppFormattingMergeBasicValue();
            baseVal.setCourtId(formattingValue.getCourtId());
            baseVal.setCppFormattingId(val.getCppFormattingId());
            baseVal.setFormattingId(formattingValue.getFormattingId());
            baseVal.setLanguage(formattingValue.getLocale().getLanguage());
            baseVal.setXhibitClobId(formattingValue.getXmlDocumentClobId());
            helper.updatePostMerge(baseVal, mergedDoc);
        }
        
        if (cppList != null) {
        	XhbClobBasicValue mergedClob = new XhbClobBasicValue();
        	mergedClob.setClobData(mergedDoc);
        	cppList.setMergedClob(mergedClob); 
			cppList.setStatus(CppListBasicValue.Status.MERGE_SUCCESSFUL);
			Long mergedClobId = updateCPPList(cppList);
			writeToCourtel(mergedClobId, formattingValue.getXmlDocumentClobId());
        }
		
	}
	
	/**
	 * Uses the values from the listdistribution.properties file 
	 * 
	 * @param docType
	 * @return
	 */
	private boolean isListLetterDocType(String docType) {
		if (docType.equals("WLS") ||
				docType.equals("FLS") ||
				docType.equals("DLS") ||
				docType.equals("ROS")) {
			return true;
		}
		return false;
	}

	/**
     * Is court a CPP court 
     * @param courtId
     * @return
     */
    private boolean isCPPCourt(Integer courtId) {
    	//get court value
    	XhbCourt court = XhbCourtBeanHelper2.findByPrimaryKey(courtId);
    	return ("Y").equals(court.getCppCourt());
    	
	}

	private StringBuffer processIWPCPPFormatting(Long xhibitClobId, FormattingValue formattingValue, Document cppDocument, String translationXml,
    		CppFormattingBasicValue val ) {
    	StringBuffer buff = null;    	
    	if(xhibitClobId!=null) {
			try {
				
				log.debug(" Found an XHIBIT document to process "+ xhibitClobId);
				buff = new StringBuffer();
				buff.append(XhbClobBeanHelper2.findByPrimaryKey(xhibitClobId).getClobData());
    			
    			Document xhibitDocument = getDocBuilderWithNoExternals(buff.toString());
    					            			
				Document mergedDocument = xmlUtils.merge(xhibitDocument,cppDocument);
				TransformerFactory transF = TransformerFactory.newInstance();
				Transformer trans = transF.newTransformer();
				StringWriter write = new StringWriter();
				trans.transform(new DOMSource(mergedDocument), new StreamResult(write));
				
				formattingValue.setReader(new StringReader(write.getBuffer().toString()));
				
				StringWriter buffer = new StringWriter();
                transform(formattingValue, translationXml, buffer);
                log.debug("\n\n\n\n\n\n\nprocessDocument(" + formattingValue + "):\n " + buffer.toString());
                      
                //insert a row into xhb clob , update clob id and write a row in the formatting merge
                CppFormattingMergeBasicValue baseVal = new CppFormattingMergeBasicValue();
                baseVal.setCourtId(formattingValue.getCourtId());
                baseVal.setCppFormattingId(val.getCppFormattingId());
                baseVal.setFormattingId(formattingValue.getFormattingId());
                baseVal.setLanguage(formattingValue.getLocale().getLanguage());
                baseVal.setXhibitClobId(xhibitClobId);
                helper.updatePostMerge(baseVal, write.getBuffer().toString()); 
                
			} catch(FinderException ex) {
		        triggerIWPProcessingError(val, ex);           			
			} catch(IOException ex) {
		        triggerIWPProcessingError(val, ex);         		       			
    		} catch(XPathExpressionException ex) {
		        triggerIWPProcessingError(val, ex);         		           			
			} catch (ParserConfigurationException ex) {
		        triggerIWPProcessingError(val, ex);         		         		
			} catch (TransformerException ex) {
		        triggerIWPProcessingError(val, ex);         		
			} catch (SAXException ex) {
		        triggerIWPProcessingError(val, ex);
			} 
		} else {
			log.error("No Xhibit documents to process");
            throw new FormattingException(" There are no Xhibit documents to process");
		}
    	return buff;
		
	}

	/**
	 * Error handling when something has gone wrong processing the IWP Merge
	 * @param val Formatting basic value
	 * @param ex the exception that has been thrown
	 */
    private void triggerIWPProcessingError(CppFormattingBasicValue val, Exception ex) {
		log.error(MERGING_ERROR+val.getCppFormattingId()+":"+ex.getMessage());
		val.setErrorMessage(MERGING_EXCEPTION_STRING+val.getCppFormattingId()+" : "+ex.getMessage());
        throw new FormattingException(MERGING_EXCEPTION_STRING+val.getCppFormattingId()+" : "+ex.getMessage(), ex);  

	}

	/**
     * Is the document one that gets sent to courtel
     * @param documentType
     * @return true if so
     */
    private boolean isCourtelSendableDoc(String documentType) {
    	boolean isSendable = false;
		if(documentType.equals("DL")|| documentType.equals("DLP")||documentType.equals("FL")||documentType.equals("WL")){
			isSendable = true;
		}
		return isSendable;
	}

	private static void transform(FormattingValue formattingValue, String translationXml, Writer buffer)
            throws TransformerException, SAXException, ParserConfigurationException, IOException {
        // Transform the output stream then flush to ensure all data has been
        // written
        Map parameterMap = createParameterMap();

        // If this is an IWP then we need to prepend the DOCTYPE tag and make other amendments
        // that the transform didnt pick up
        if (formattingValue.getDocumentType().equals(IWP)) {
            log.debug("Processing a IWP type");
            // Creating a dummy OutputStream so that the CORRECT outputstream doesnt get written to before it should
            // as OutputStreams cannot be amended once written to - we will get the output from the Buffer
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            createTransformer(formattingValue, parameterMap).transform(
                    createSource(formattingValue, translationXml, parameterMap), createResult(baos, formattingValue, buffer));

            StringBuffer sb = new StringBuffer("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n");
            if (buffer == null) {
                log.warn("IWP: buffer is null; pages will NOT be generated.");
            } else {
                String updatedPage = amendGeneratedPage(buffer.toString());
                sb.append(updatedPage);
                log.debug("updated page is "+sb.toString());
                buffer = new StringWriter();
                for (int i=0; i< sb.length(); i++) {
                    buffer.append(sb.charAt(i));
                }

                // Now write the CORRECT output to the CORRECT output stream
                if (formattingValue.getOutputPath() != null) { // This one is used for testing
                    FileOutputStream fos = (FileOutputStream)formattingValue.getOutputStream();
                    fos.write(sb.toString().getBytes());
                    
                    formattingValue.setOutputStream(fos);
                } else { // ByteArrayOutputStream or OracleBlobOutputStream
                    if (formattingValue.getOutputStream() instanceof ByteArrayOutputStream) {
                        ByteArrayOutputStream nbaos = (ByteArrayOutputStream) formattingValue.getOutputStream();
                        nbaos.write(sb.toString().getBytes());
                        
                        formattingValue.setOutputStream(nbaos);
                    } else if (formattingValue.getOutputStream() instanceof OutputStream) { // Used to output html
                        OutputStream bos = ((OutputStream) formattingValue.getOutputStream());
                        log.debug("\n\n\nAbout to write to outputstream the page :\n"+sb.toString()+"\n\n\n");
                        bos.write(sb.toString().getBytes());
                        formattingValue.setOutputStream(bos);
                    } else {
                        log.warn("formattingValue is not a ByteArrayOutputStream and output path is undefined - this shouldn't happen");
                    }
                }
            }
        } else { // Do it as it did priot to the RFC 2787 changes
            createTransformer(formattingValue, parameterMap).transform(
                    createSource(formattingValue, translationXml, parameterMap), createResult(formattingValue, buffer));
        }
        
        formattingValue.getOutputStream().flush();
    }
    
    /**
     * Add amendments as part of 2787 web page redesign that the transform is not picking up (but should!!)
     * @param inPage
     * @return
     */
    private static String amendGeneratedPage(String inPage) {
        String textToInsert = "\r\n<!--[if IE 7]>\r\n<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.justice.gov.uk/css/ie7.css\" />\r\n" +
                              "<![endif]-->\r\n<!--[if IE 6]>\r\n" +
                              "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.justice.gov.uk/css/ie6.css\" />\r\n<![endif]-->";
        
        StringBuffer amendedPage = new StringBuffer(inPage);
        int x = amendedPage.indexOf("print-listings.css");
        char c = amendedPage.charAt(x+19);
        char c1 = amendedPage.charAt(x+20);
        if ((c == '/') && (c1 == '>')) {
            amendedPage.insert(x+21, textToInsert);
        }
        
        return amendedPage.toString();
    }

    private static Source createSource(FormattingValue formattingValue, String translationXml, Map parameterMap)
            throws TransformerException, SAXException, ParserConfigurationException {
        String[] xsltNames = FormattingConfig.getInstance().getXslTransforms(formattingValue);
        // Get Templates
        Templates[] templatesArray = CSServices.getXSLServices().getTemplatesArray(xsltNames,
                formattingValue.getLocale(), parameterMap);
        // Wrap Templates
        for (int i = 0; i < templatesArray.length; i++) {
            templatesArray[i] = new TranslationXMLTemplates(translationXml, templatesArray[i]);
        }
        // Use Templates
        return createSource(formattingValue, formattingValue.getReader(), templatesArray);
    }

    private static Source createSource(FormattingValue formattingValue, Reader reader, Templates[] templatesArray)
            throws TransformerException, SAXException, ParserConfigurationException {
        if (templatesArray.length == 0) {
            // No transforms to perform so use in directly
            return new StreamSource(reader);
        } else {
            // Create and link the filters
            XMLFilter[] filters = CSServices.getXSLServices().getFilters(templatesArray);
            if (0 < filters.length) {
                filters[0].setParent(createReader());
                for (int i = 1; i < filters.length; i++) {
                    filters[i].setParent(filters[i - 1]);
                }
            }
            return new SAXSource(filters[filters.length - 1], new InputSource(reader));
        }
    }

    private static XMLReader createReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser parser = spf.newSAXParser();
        return parser.getXMLReader();
    }
    
    private static Result createResult(ByteArrayOutputStream baos, FormattingValue formattingValue, Writer buffer) throws IOException {
        return createResult(baos, formattingValue.getMimeType(), buffer);
    }

    private static Result createResult(FormattingValue formattingValue, Writer buffer) throws IOException {
        return createResult(formattingValue.getOutputStream(), formattingValue.getMimeType(), buffer);
    }

    private static Result createResult(OutputStream out, String mimeType, Writer buffer) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("createResult(" + toDebug(out) + "," + mimeType + "," + toDebug(buffer) + ")");
        }

        // If PDF pipe into a fop driver before output else direct into output
        if (mimeType.equalsIgnoreCase("PDF")) {
            return createPdfResult(out, buffer);
        } else if (mimeType.equalsIgnoreCase("HTM")) {
            return createHtmlResult(out, buffer);
        } else {
            return createXmlResult(out, buffer);
        }
    }

    private static Result createPdfResult(OutputStream out, Writer buffer) throws IOException {
        if (buffer != null) {
            return new SAXResult(new ForkContentHandler(createPdfSerializer(out), createXmlSerializer(buffer)));
        } else {
            return new SAXResult(createPdfSerializer(out));
        }
    }

    private static Result createHtmlResult(OutputStream out, Writer buffer) throws IOException {
        if (buffer != null) {
            return new SAXResult(new ForkContentHandler(createHtmlSerializer(out), createHtmlSerializer(buffer)));
        } else {
            return new SAXResult(createHtmlSerializer(out));
        }
    }

    private static Result createXmlResult(OutputStream out, Writer buffer) throws IOException {
        if (buffer != null) {
            return new SAXResult(new ForkContentHandler(createXmlSerializer(out), createXmlSerializer(buffer)));
        } else {
            return new SAXResult(createXmlSerializer(out));
        }
    }

    private static ContentHandler createXmlSerializer(OutputStream out) throws IOException {
        return CSServices.getXMLServices().createXmlSerializer(out);
    }

    private static ContentHandler createXmlSerializer(Writer writer) throws IOException {
        return CSServices.getXMLServices().createXmlSerializer(writer);
    }

    private static ContentHandler createHtmlSerializer(OutputStream out) throws IOException {
        return CSServices.getXMLServices().createHtmlSerializer(out);
    }

    private static ContentHandler createHtmlSerializer(Writer writer) throws IOException {
        return CSServices.getXMLServices().createHtmlSerializer(writer);
    }
    
    
    private static ContentHandler createPdfSerializer(OutputStream out) throws IOException {

    	try {
	    	FOUserAgent agent = fopFactory.newFOUserAgent();
	        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, agent, out);
	        
	        return fop.getDefaultHandler();

    	} catch (FOPException fope) {
            throw new IOException(fope);
        }
    }

    private static Transformer createTransformer(FormattingValue formattingValue, Map parameterMap)
            throws TransformerConfigurationException, TransformerFactoryConfigurationError {

        // Create a new transformer with the parameters
        Transformer transformer = CSServices.getXSLServices().getTransformer(formattingValue.getLocale(), parameterMap); 
        if (formattingValue.getDocumentType().equals(IWP)) {

            if (log.isDebugEnabled()) {
                log.debug("Setting transformer output properties for XHTML generation");
            }
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD XHTML 1.0 Strict//EN");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Created transformer " + transformer.getClass().getName() + " " + transformer.getURIResolver()
                    + ".");
            Properties p = transformer.getOutputProperties();
            Enumeration e = p.elements();
            
            while (e.hasMoreElements()) {
                String key = e.nextElement().toString();
                log.debug(key + " -- " + p.getProperty(key));
            }
        }
        return transformer;
    }

    private static Map createParameterMap() {
        // Create parameter map
        Map parameterMap = new HashMap();
        synchronized (javaDateFormat) {
            parameterMap.put("java-date", javaDateFormat.format(new Date()));
            parameterMap.put("method","xml");
            parameterMap.put("doctype-system","http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            parameterMap.put("doctype-public", "-//W3C//DTD XHTML 1.0 Strict//EN");
        }
        return parameterMap;
    }

    private static String toDebug(Writer writer) {
        return writer == null ? "null" : writer.getClass().getName();
    }

    private static String toDebug(OutputStream out) {
        return out == null ? "null" : out.getClass().getName();
    }

    //
    // Utiltity classes
    //	

    /**
     * This class is used to wrap
     */
    private static class TranslationXMLTemplates implements Templates {

        private final String translationXml;

        private final Templates delegate;

        /**
         * Constuct a new Translation XML Template wrapper
         * 
         * @param translationXml
         *            (optional)
         * @param delegate
         */
        public TranslationXMLTemplates(String translationXml, Templates delegate) {
            if (delegate == null) {
                throw new IllegalArgumentException("delegate: null");
            }
            this.translationXml = translationXml;
            this.delegate = delegate;
        }

        /**
         * Create a new transformer wrapping the resolver with a resolver that
         * returns the translations.
         */
        public Transformer newTransformer() throws TransformerConfigurationException {
            Transformer transformer = delegate.newTransformer();
            if (transformer != null) {
                transformer.setURIResolver(new TranslationXMLURIResolver(translationXml, transformer.getURIResolver()));
            }
            return transformer;
        }

        /**
         * Get the output properties for the delegate
         */
        public Properties getOutputProperties() {
            return delegate.getOutputProperties();
        }

        /**
         * Resolve the translation xml entity to the translation XML
         */
        private static class TranslationXMLURIResolver implements URIResolver {

            private final String translationXml;

            private final URIResolver delegate;

            /**
             * Construct a new instance;
             * 
             * @param translationXml
             *            (optional)
             * @param delegate
             *            (optional)
             */
            public TranslationXMLURIResolver(String translationXml, URIResolver delegate) {
                this.translationXml = translationXml;
                this.delegate = delegate;
            }

            /**
             * URIResolver Implementation, resolve the name to the source
             * 
             * @param href
             *            An href attribute, which may be relative or absolute.
             * @param base
             *            The base URI in effect when the href attribute was
             *            encountered.
             * @return A Source object, or null if the href cannot be resolved,
             *         and the processor should try to resolve the URI itself.
             * @throws TransformerException -
             *             if an error occurs when trying to resolve the URI.
             */
            public Source resolve(String href, String base) throws TransformerException {
                if ("translation.xml".equals(href)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Resolved url \"translation.xml\" to cached translation xml.");
                    }

                    // If we have translation xml return it
                    if (translationXml != null) {
                        return new StreamSource(new StringReader(translationXml), "translation.xml");
                    }
                } else if (delegate != null) {
                    // If we have a delegate allow it to try
                    return delegate.resolve(href, base);
                }
                // Allow system to have a go
                return null;
            }
        }
    }

    // Main Method & Helpers For Debug

    /**
     * Main method to allow formating to be run from the console, this is very
     * useful for debuging.
     */
    public static void main(String args[]) {

        // Set the defaults
        String distributionType = null;
        String mimeType = null;
        String documentType = null;
        Integer majorVersion = null;
        Integer minorVersion = null;
        String language = null;
        String country = null;
        String inFilePath = null;
        String translationXmlFilePath = null;
        String translationXmlFileEncoding = null;
        Integer courtId=null;

        // Process the command line
        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                if (++i < args.length) {
                    distributionType = args[i];
                } else {
                    help();
                }
            } else if ("-m".equals(args[i])) {
                if (++i < args.length) {
                    mimeType = args[i];
                } else {
                    help();
                }
            } else if ("-t".equals(args[i])) {
                if (++i < args.length) {
                    documentType = args[i];
                } else {
                    help();
                }
            } else if ("-v".equals(args[i])) {
                if (++i < args.length) {
                    try {
                        majorVersion = new Integer(args[i]);
                    } catch (NumberFormatException nfe) {
                        help();
                    }
                } else {
                    help();
                }
            } else if ("-n".equals(args[i])) {
                if (++i < args.length) {
                    try {
                        minorVersion = new Integer(args[i]);
                    } catch (NumberFormatException nfe) {
                        help();
                    }
                } else {
                    help();
                }
            } else if ("-l".equals(args[i])) {
                if (++i < args.length) {
                    language = args[i];
                } else {
                    help();
                }
            } else if ("-c".equals(args[i])) {
                if (++i < args.length) {
                    country = args[i];
                } else {
                    help();
                }
            } else if ("-ci".equals(args[i])) {
                if (++i < args.length) {
                    try {
                        courtId = Integer.valueOf(args[i]);
                    } catch (NumberFormatException nfe) {
                        help();
                    }
                } else {
                    help();
                }
            }else if ("-x".equals(args[i])) {
                if (++i < args.length) {
                    translationXmlFilePath = args[i];
                } else {
                    help();
                }
            } else if ("-e".equals(args[i])) {
                if (++i < args.length) {
                    translationXmlFileEncoding = args[i];
                } else {
                    help();
                }
            } else {
                inFilePath = args[i];
            }
        }

        // Check Mandatory Values
        if (courtId == null || distributionType == null || mimeType == null || documentType == null || inFilePath == null) {
            help();
        }
        // Process
        try {
            process(distributionType, mimeType, documentType, majorVersion, minorVersion, language, country,
                    translationXmlFilePath, translationXmlFileEncoding, inFilePath, courtId);
        } catch (Exception e) {
            log.error("An error occured formatting document.", e);
        }

    }

    private static final void help() {
        System.out.println("Usage: java " + FormattingServices.class.getName() + " [-options] inFilePath");
        System.out.println("where options include:");
        System.out.println("-ci courtId (required)");
        System.out.println("-d distributionType (required)");
        System.out.println("-m mimeType (required)");
        System.out.println("-t documentType (required)");
        System.out.println("-v majorVersion (optional)");
        System.out.println("-n minorVersion (optional)");
        System.out.println("-l language (optional)");
        System.out.println("-c country (optional)");
        System.out.println("-x translationXmlFilePath (optional)");
        System.out.println("-e translationXmlFileEncoding (optional) uses UTF-8 if unspecified.");
        System.exit(0);
    }

    private static final void process(String distributionType, String mimeType, String documentType,
            Integer majorVersion, Integer minorVersion, String language, String country, String translationXmlFilePath,
            String translationXmlFileEncoding, String inFilePath, Integer courtId) throws Exception {
        FormattingServices formattingServices = new FormattingServices();

        java.io.File inFile = new java.io.File(inFilePath);
        if (inFile.isFile()) {
            process(formattingServices, distributionType, mimeType, documentType, majorVersion, minorVersion, language,
                    country, translationXmlFilePath, translationXmlFileEncoding, inFile, courtId);
        } else if (inFile.isDirectory()) {
            java.io.File[] inFiles = inFile.listFiles();
            for (int i = 1; i < inFiles.length; i++) {
                process(formattingServices, distributionType, mimeType, documentType, majorVersion, minorVersion,
                        language, country, translationXmlFilePath, translationXmlFileEncoding, inFiles[i], courtId);
            }
        }
    }

    private static final void process(FormattingServices formattingServices, String distributionType, String mimeType,
            String documentType, Integer majorVersion, Integer minorVersion, String language, String country,
            String translationXmlFilePath, String translationXmlFileEncoding, java.io.File inFile, Integer courtId) throws Exception {
        String outputPath = getOutFilePath(inFile.getAbsolutePath(), mimeType);
        formattingServices.processDocument(new FormattingValue(distributionType, mimeType, documentType, majorVersion,
                minorVersion, language, country, new java.io.FileReader(inFile), 
                new java.io.FileOutputStream(outputPath), outputPath, courtId),
                getTranslationXml(translationXmlFilePath, translationXmlFileEncoding));
    }

    private static final String getOutFilePath(String inFilePath, String mimeType) {
        {
            String oldExtension = ".XML";
            String newExtension = "." + mimeType.toLowerCase();

            String pathNoExtension;
            if (inFilePath.toUpperCase().endsWith(oldExtension)) {
                pathNoExtension = inFilePath.substring(0, inFilePath.length() - oldExtension.length());
            } else {
                pathNoExtension = inFilePath;
            }

            return pathNoExtension + newExtension;
        }
    }

    private static final String getTranslationXml(String translationXmlFilePath, String translationXmlFileEncoding)
            throws IOException {
        if (translationXmlFilePath != null) {
            java.io.Reader fileReader =null;
            java.io.Reader reader = null;
            try {
                StringBuffer buffer = new StringBuffer();

                if (translationXmlFileEncoding != null) {
                    fileReader = new java.io.InputStreamReader(new java.io.FileInputStream(translationXmlFilePath),
                            translationXmlFileEncoding);
                } else {
                    fileReader = new java.io.InputStreamReader(new java.io.FileInputStream(translationXmlFilePath),
                            "UTF-8");
                }

                reader = new java.io.BufferedReader(fileReader);

                // Strip off BOM if first char, Java Bug 4508058 workaround
                int c = reader.read();
                if (c != -1 && c != 0xFEFF) {
                    buffer.append((char) c);
                }

                // Append rest of file
                c = reader.read();
                while (c != -1) {
                    buffer.append((char) c);
                    c = reader.read();
                }

                return buffer.toString();
            } catch (java.io.IOException ioe) {
                log.warn("An error occured reading translation xml file \"" + translationXmlFilePath + "\".", ioe);
            } finally {
            	closeReaders(fileReader, reader);
            	
            }
        }
        return null;
    }

    /**
     * Close readers.
     * @param reader []
     */
    private static void closeReaders(final Reader ...reader) {
    	for(int i=0;i<reader.length;i++) {
	    	if(reader[i]!=null) {
	    		try {
	    			reader[i].close();
	    		} catch(IOException ioe) {
	    			log.warn("an error occurred trying to close the file reader"+ioe);
	    		}
	    	}	
    	}
	}

	/**
     * This is a public method so that it can be used by other areas of the CPP interface
     * 
     * @param documentType
     * @return
     */
    public static AbstractXMLMergeUtils getXMLUtils(String documentType) {
    	try {
    		AbstractXMLMergeUtils xmlUtils = null;
	    	if ("DL".equals(documentType) ||
	    			"DLL".equals(documentType) ||
	    			"DLP".equals(documentType)) {
	    		xmlUtils = new DailyListXMLMergeUtils();
	    	} else if ("FL".equals(documentType) ||
	    			"FLL".equals(documentType)) {
	    		xmlUtils = new FirmListXMLMergeUtils();
	    	} else if ("WL".equals(documentType) ||
	    			"WLL".equals(documentType)) {
	    		xmlUtils = new WarnedListXMLMergeUtils();
	    	} else if (IWP.equals(documentType)) {
	    		xmlUtils = new IWPXMLMergeUtils();		
	    	}
	    	return xmlUtils;
    	} catch (XPathExpressionException e) {
    		CSServices.getDefaultErrorHandler().handleError(e, FormattingServices.class);
    		throw new FormattingException(" An error has occured during the setup of the xmlUtils process", e);
		}    	
    }
    
    private Long updateCPPList(final CppListComplexValue cppList) {
    	try {
    		return cppListHelper.updateCppList(cppList, "XHIBIT");
		} catch (FinderException e) {
			CSServices.getDefaultErrorHandler().handleError(e, FormattingServices.class);
			throw new FormattingException(" An error has occured during the update of the CPP List", e);
		}
    }
    
    /**
     * Write the information to courtel table if it doesn't already exist in there 
     * @param mergedDocumentClobId the Merged clob id
     * @param originalClobId the original clob
     * @throws EJBException
     */
    private void writeToCourtel(Long mergedDocumentClobId, Long originalClobId) throws EJBException {
    	
    	Collection numberOfXmlDocumentClobRows = null;
    	Collection numberOfXmlIdRows = null;
    	
	    XhbCourtelListBasicValue basicValue = new XhbCourtelListBasicValue();
		basicValue.setSentToCourtel("N");
		basicValue.setNumSendAttempts(0);
	
	    if(mergedDocumentClobId!=null) {
	    	basicValue.setXmlDocumentClobId(mergedDocumentClobId);
	    	numberOfXmlDocumentClobRows = XhbCourtelListBeanHelper2.findCountOfXmlDocClobId(mergedDocumentClobId);
	    }
	    	
		//set the xhb xml document - needed to do the filename to send
		ArrayList<XhbXmlDocument> documents = (ArrayList<XhbXmlDocument>)XhbXmlDocumentBeanHelper2.findByXmlDocumentClobId(originalClobId);
		if(documents!=null && !documents.isEmpty()){
			XhbXmlDocument doc = documents.get(0);
			basicValue.setXmlDocumentId(doc.getXmlDocumentId());
			//check the xhb_courtel_list table to see if any entries in there with xmlDocumentId set to doc.getXmlDocumentId 
			numberOfXmlIdRows = XhbCourtelListBeanHelper2.findCountOfXmlId(doc.getXmlDocumentId());
			
			boolean isXmlDocFound = numberOfXmlDocumentClobRows!=null && !numberOfXmlDocumentClobRows.isEmpty();
			boolean isXmlIdFound = numberOfXmlIdRows!=null && !numberOfXmlIdRows.isEmpty();
			//if not then create
			if(!isXmlDocFound && !isXmlIdFound){
				if (log.isDebugEnabled()) {
					log.debug("About to write to courtel for "+doc.getXmlDocumentId());
				}
				
				XhbCourtelListBeanHelper2.create(basicValue);	
				//else do nothing as it's already been created(for PDF or HTM whichever one is first)
			}
			else if (log.isDebugEnabled()) {
				log.debug("The entry already exists for "+doc.getXmlDocumentId());
			}
				
		} else {
			//this shouldn't happen as this process wouldn't work without this row so throw exception
			//if this does occurr.
			throw new FormattingException("List couldn't be merged as no xml document was found");
		}
    }
    
    /**
     * Bring back all the court sites.  This is used for merging CPP to XHIBIT documents.
     * @param document Document containing the court sites
     * @return a list of courtsites
     * @throws XPathExpressionException
     */
    public static List<Node> getCourtSites(Document document) throws XPathExpressionException {
		List<Node> results = new ArrayList<Node>();
		 String [] rootNodes = new String[] {"currentcourtstatus/court/courtsites/courtsite/courtsitename"};
		XPathExpression[] rootNodeExpression= new XPathExpression[rootNodes.length];
		for (int nodeNo = 0; nodeNo < rootNodes.length; nodeNo++) {
			rootNodeExpression[nodeNo] = XPathFactory.newInstance().newXPath().compile(rootNodes[nodeNo]);
		} 

		for (int rootNodeNo = 0; rootNodeNo < rootNodeExpression.length; rootNodeNo++) {
			NodeList nodeList = (NodeList) rootNodeExpression[rootNodeNo].evaluate(document, XPathConstants.NODESET);
			for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
				if(Node.ELEMENT_NODE == nodeList.item(nodeNo).getNodeType() && "courtsitename".equals((nodeList.item(nodeNo).getNodeName()))){
					Node el = ((Element)nodeList.item(nodeNo)).getFirstChild();
					if(el !=null) {
						results.add(el);
					}
					
				}
			}
		}
		
		
		return results;
	}
    
    /**
     * Replaces the court site name from cpp with the list name (as this is what warned list needs).
     * @param document Document containing the court sites
     * @return a list of courtsites
     * @throws XPathExpressionException
     */
    public static Document replaceCourtSiteWithListName(Document document, Integer courtId) throws XPathExpressionException {
    	
    	XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodesList = (NodeList)xpath.evaluate("WarnedList/CourtLists/CourtList/CourtHouse/CourtHouseName",document,XPathConstants.NODESET);
		
		for (int nodeNo = 0; nodeNo < nodesList.getLength(); nodeNo++) {
			Node el = ((Element)nodesList.item(nodeNo)).getFirstChild();
			if(el !=null) {
				String courtSiteName = el.getTextContent().replaceAll("\n", "").replace("\r", "");
			    XhbCourtSiteBasicValue courtSite = XhbCourtSiteBeanHelper2.findByCourtIdAndCourtSiteNameValue(courtId, courtSiteName);
			    String toReplace = "at "+courtSite.getListName();
			    el.setTextContent(toReplace);
			}
		}			
		return document;
	}

	/**
     * Save the document information for IWP so that broker picks up the entry.
     * @param formattingValue FormattingValue
     * @param courtsite String
     */
    private void saveDocInfo(FormattingValue formattingValue, String courtsite) {
    	if (log.isDebugEnabled()) {
             log.debug("About to save the courtsite information for formatting id "+formattingValue.getFormattingId()+" and courtsite "+courtsite);
         }
    	
    	//insert rows into xhb_document_control and xhb_document_recipient
		XhbDocumentControlBasicValue bv1 = new XhbDocumentControlBasicValue();
		bv1.setStatus("ND");
		bv1.setDistributionType("FTP");
		bv1.setMimeType("HTM");
		bv1.setDocumentType("IWP");
		bv1.setFormattingId(formattingValue.getFormattingId());
		bv1.setCourtId(formattingValue.getCourtId());
		bv1.setLanguage(formattingValue.getLocale().getLanguage());
		bv1.setCountry(formattingValue.getLocale().getCountry());
		
		XhbDocumentControlBasicValue returned = XhbDocumentControlBeanHelper2.create(bv1);
		
		XhbDocumentRecipientBasicValue bv2 = new XhbDocumentRecipientBasicValue();
		StringBuffer docRecipient = new StringBuffer(formattingDatabaseProcedures.getDocRecipientName(courtsite));
		if(bv1.getLanguage().equalsIgnoreCase("cy")) {
			if (log.isDebugEnabled()) {
	             log.debug("File to save is cymraeg");
	         }
			docRecipient.append("_cy");
		}
		bv2.setDocRecipientName(docRecipient.toString());
		bv2.setDocControlId(returned.getDocControlId());	
		XhbDocumentRecipientBeanHelper2.create(bv2);
    	
    }
    
    /**
	 * Returns a document with externals not allowed.
	 * This was flagged up by sonarqube.
	 * @param xml1 
	 * @return Document
	 * @throws ParserConfigurationException
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private Document getDocBuilderWithNoExternals(String xml1) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xml1)));
	}
	
	/**
	 * Returns true if merge is allowed i.e. the time is 
	 * before the cut off time defined in the db.
	 * @return boolean
	 */
	private boolean isMergeAllowed() {
		log.debug("About to check if a merge is allowed");
		String[] mergeCutOffTime = XhbConfigPropBeanHelper2.findByPropertyNameValue("MERGE_CUT_OFF_TIME")[0].getPropertyValue().split(":");
		if(mergeCutOffTime!=null && mergeCutOffTime.length==3) {
	    	Calendar now = Calendar.getInstance();
	    	Calendar mergeCutOff = Calendar.getInstance();
	    	mergeCutOff.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mergeCutOffTime[0]));
	    	mergeCutOff.set(Calendar.MINUTE, Integer.parseInt(mergeCutOffTime[1]));
	    	mergeCutOff.set(Calendar.SECOND,Integer.parseInt( mergeCutOffTime[2]));
	    	
	    	log.debug("Is "+mergeCutOff.getTime()+" after "+now.getTime());
	    	return mergeCutOff.after(now);
    	}
    	return false;
	}
	
	
	/************************************ PDF Printing Helper methods *************************/
	/**
     * Returns the FopFactoryBuilder with backwards compatibility
     * 
     * @param Uri uri
     * @return The new FopFactoryBuilder
     */
    private static FopFactoryBuilder getFopFactoryBuilder(final URI uri) {
		FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(uri);
    	// Set the Fop to backwards compatibility validation
    	fopFactoryBuilder.setStrictFOValidation(false);
    	return fopFactoryBuilder;
    }
    
    /**
     * Returns the equivalent of new FopFactory.newInstance
     * 
     * @param Uri uri
     * @return The new FopFactory
     */
    private static FopFactory getFopFactory(final URI uri) {
    	return getFopFactoryBuilder(uri).build();
    }
    
}