package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier.XhbPddaDlNotifierDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DefinitivePublicNoticeStatusValue;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicNoticeValue;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DummyPdNotifierUtil {

    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";
    private static final String NO = "N";
    private static final String EQUALS = "Results are not Equal";
    private static final String NOTEQUALS = "Results are Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";

    private DummyPdNotifierUtil() {
        // Do nothing
    }

    public static XhbPddaDlNotifierDao getXhbPddaDlNotifierDao(Integer courtId,
        LocalDateTime lastRunDate) {
        Integer pddaDlNotifierId = Double.valueOf(Math.random()).intValue();
        String status = null;
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now().minusHours(1);
        LocalDateTime creationDate = LocalDateTime.now().minusDays(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 1;
        String obsInd = NO;
        XhbPddaDlNotifierDao result = new XhbPddaDlNotifierDao();
        result.setPddaDlNotifierId(pddaDlNotifierId);
        result.setCourtId(courtId);
        result.setLastRunDate(lastRunDate);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return result;
    }

    public static XhbPddaMessageDao getXhbPddaMessageDao() {
        Integer pddaMessageId = -99;
        Integer courtId = 81;
        Integer courtRoomId = 104;
        String pddaMessageGuid = "ThisIsGUID";
        Integer pddaMessageTypeId = -299;
        Long pddaMessageDataId = Long.valueOf(-199);
        Integer pddaBatchId = null;
        LocalDateTime timeSent = null;
        String cpDocumentName = "cpDocumentName";
        String cpDocumentStatus = "cpDocumentStatus";
        String cpResponseGenerated = "cpResponseGenerated";
        String errorMessage = null;
        Integer cppStagingInboundId = -9;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        String obsInd = "N";

        XhbPddaMessageDao result = new XhbPddaMessageDao();
        result.setPddaMessageId(pddaMessageId);
        result.setCourtId(courtId);
        result.setCourtRoomId(courtRoomId);
        result.setPddaMessageGuid(pddaMessageGuid);
        result.setPddaMessageTypeId(pddaMessageTypeId);
        result.setPddaMessageDataId(pddaMessageDataId);
        result.setPddaBatchId(pddaBatchId);
        result.setTimeSent(timeSent);
        result.setCpDocumentName(cpDocumentName);
        result.setCpDocumentStatus(cpDocumentStatus);
        result.setCpResponseGenerated(cpResponseGenerated);
        result.setErrorMessage(errorMessage);
        result.setCppStagingInboundId(cppStagingInboundId);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);

        return new XhbPddaMessageDao(result);
    }

    public static XhbRefPddaMessageTypeDao getXhbRefPddaMessageTypeDao() {
        Integer refPddaMessageTypeId = -299;
        String pddaMessageType = "messageType";
        String pddaMessageTypeDescription = "messageTypeDescription";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        String obsInd = "N";
        XhbRefPddaMessageTypeDao result = new XhbRefPddaMessageTypeDao(refPddaMessageTypeId,
            pddaMessageType, pddaMessageTypeDescription, lastUpdateDate, creationDate,
            lastUpdatedBy, createdBy, version, obsInd);
        return new XhbRefPddaMessageTypeDao(result);
    }

    public static XhbCppStagingInboundDao getXhbCppStagingInboundDao() {
        Integer cppStagingInboundId = 2;
        String documentName = "WebPage_453_20201009170000";
        String courtCode = "453";
        String documentType = "WP";
        LocalDateTime timeLoaded = LocalDateTime.now();
        Long clobId = (long) 1_539_815;
        String validationStatus = "VS";
        String acknowledgmentStatus = null;
        String processingStatus = "SP";
        String validationErrorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        String obsInd = "N";
        XhbCppStagingInboundDao result = new XhbCppStagingInboundDao();
        result.setCppStagingInboundId(cppStagingInboundId);
        result.setDocumentName(documentName);
        result.setCourtCode(courtCode);
        result.setDocumentType(documentType);
        result.setTimeLoaded(timeLoaded);
        result.setClobId(clobId);
        result.setValidationStatus(validationStatus);
        result.setAcknowledgmentStatus(acknowledgmentStatus);
        result.setProcessingStatus(processingStatus);
        result.setValidationErrorMessage(validationErrorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbCppStagingInboundDao(result);
    }

    public static DefinitivePublicNoticeStatusValue getDefinitivePublicNoticeStatusValue() {
        Integer definitivePublicNoticeId = -1;
        boolean isActive = true;
        return new DefinitivePublicNoticeStatusValue(definitivePublicNoticeId, isActive);
    }

    public static DisplayablePublicNoticeValue getDisplayablePublicNoticeValue() {
        DisplayablePublicNoticeValue result = new DisplayablePublicNoticeValue();
        result.setDesc("Desc");
        result.setPriority(2);
        result.setDesc(result.getDesc());
        result.setPriority(result.getPriority());
        result.setIsActive(result.isActive());
        result.setDefinitivePublicNotice(result.getDefinitivePublicNotice());
        result.setDirty(result.isDirtyFlagged());
        DisplayablePublicNoticeValue compareTo = new DisplayablePublicNoticeValue();
        compareTo.setPriority(3);
        assertNotEquals(0, result.compareTo(compareTo), NOTEQUALS);
        compareTo.setPriority(1);
        assertNotEquals(0, result.compareTo(compareTo), NOTEQUALS);
        compareTo.setPriority(result.getPriority());
        assertEquals(0, result.compareTo(compareTo), EQUALS);
        Boolean isEquals = result.equals(compareTo);
        assertFalse(isEquals, "Result is not False");
        assertNotEquals(compareTo.hashCode() * -1, result.hashCode(), NOTEQUALS);
        return result;
    }

    public static XhbConfiguredPublicNoticeDao getXhbConfiguredPublicNoticeDao(String active) {
        Integer configuredPublicNoticeId = -1;
        Integer courtRoomId = -1;
        Integer publicNoticeId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;

        XhbConfiguredPublicNoticeDao result =
            new XhbConfiguredPublicNoticeDao(configuredPublicNoticeId, active, courtRoomId,
                publicNoticeId, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        result.setXhbPublicNotice(result.getXhbPublicNotice());
        return new XhbConfiguredPublicNoticeDao(result);
    }

    public static PublicNoticeValue getPublicNoticeValue() {
        PublicNoticeValue result = new PublicNoticeValue();
        result.setPriority(result.getPriority());
        result.setPublicNoticeDesc("Desc");
        result.setPriority(2);
        assertFalse(result.isActive(), FALSE);
        result.setActive(true);
        assertTrue(result.isActive(), TRUE);
        assertTrue(result.hasInformationForDisplay(), TRUE);
        return result;
    }
}
