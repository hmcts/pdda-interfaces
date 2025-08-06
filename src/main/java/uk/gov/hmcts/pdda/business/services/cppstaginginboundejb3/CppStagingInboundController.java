package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import jakarta.ejb.Remote;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Remote
public interface CppStagingInboundController extends Serializable {

    /**

     * Returns the latest unprocessed XHB_CPP_STAGING_INBOUND record for processing.

     * @return CppStagingInboundBasicValue
     * @throws CppStagingInboundControllerException Exception
     */
    List<XhbCppStagingInboundDao> getLatestUnprocessedDocument();

    /**

     * Returns the latest record from XHB_CPP_STAGING_INBOUND that has been validated successfully
     * and not processed.

     * @return CppStagingInboundBasicValue
     * @throws CppStagingInboundControllerException Exception
     */
    List<XhbCppStagingInboundDao> getNextValidatedDocument();

    /**

     * Returns the earliest XHB_CPP_STAGING_INBOUND from today that is to be validated.

     * @return CppStagingInboundBasicValue
     * @throws CppStagingInboundControllerException Exception
     */
    List<XhbCppStagingInboundDao> getNextDocumentToValidate();

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record with a status of successfully validated.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     */
    void updateStatusSuccess(XhbCppStagingInboundDao cppStagingInboundDao,
        String userDisplayName);

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record with a status of validation failed.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     */
    void updateStatusFailed(XhbCppStagingInboundDao cppStagingInboundDao,
        String reasonForFail, String userDisplayName);

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record with a status of In Progress.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     * @return XhbCppStagingInboundDao
     */
    Optional<XhbCppStagingInboundDao> updateStatusInProcess(
        XhbCppStagingInboundDao cppStagingInboundDao, String userDisplayName);

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record with a processing status of fail.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     */
    void updateStatusProcessingFail(XhbCppStagingInboundDao cppStagingInboundDao,
        String reasonForFail, String userDisplayName);

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record with a processing status of fail.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     */
    void updateStatusProcessingSuccess(XhbCppStagingInboundDao cppStagingInboundDao,
        String userDisplayName);

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record such that all status values are reset back to when
     * there initial values This is useful for testing.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     */
    void resetDocumentStatus(XhbCppStagingInboundDao cppStagingInboundDao,
        String userDisplayName);

    /**
     * Validates document from XHB_STAGING_INBOUND where the current VALIDATION_STATUS='IP' This
     * document is validated as follows: 1. The DOCUMENT_NAME is checked to follow a valid format 2.
     * The DOCUMENT_TYPE is checked to be valid 3. The appropriate schema to validate the XML
     * against will be determined 4. Validation of the XML will be done against the appropriate
     * schema.

     * @param cppStagingInboundDao XhbCppStagingInboundDao
     * @param userDisplayName String
     */
    boolean validateDocument(XhbCppStagingInboundDao cppStagingInboundDao,
        String userDisplayName)
        throws uk.gov.hmcts.pdda.business.services.validation.ValidationException;

    /**
     * Based on the document type return the (name of the) schema that is to be used to validate the
     * XML The schema document itself will be picked up from SCHEMA_DIR as defined at the top of the
     * class.

     * @param documentType String
     * @return String
     */
    String getSchemaName(String documentType);

    /**
     * Get courtId from XHB_COURT_SITE using crest court id (aka court code).

     * @param courtCode Integer
     * @return int
     */
    int getCourtId(Integer courtCode);

    /**
     * Given a clob Id get the Xml.

     * @param clobId Long
     * @return String
     */
    String getClobXmlAsString(Long clobId);
}
