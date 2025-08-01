package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * PddaMessageUtil.
 **/
public final class PddaMessageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PddaMessageUtil.class);

    private static final DateTimeFormatter DATETIMEFORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String YES = "Y";
    private static final String ACKNOWLEDGE_SUCCESS = "AS";

    private PddaMessageUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void createMessage(
        final PddaMessageHelper pddaMessageHelper, final Integer courtId, final Integer courtRoomId,
        final Integer pddaMessageTypeId, final Long pddaMessageDataId, final Integer pddaBatchId,
        final String cpDocumentName, final String cpResponseGenerated, final String errorMessage) {
        LOG.debug("createMessage({},{},{},{},{},{},{},{},{})", pddaMessageHelper, courtId,
            courtRoomId, pddaMessageTypeId, pddaMessageDataId, pddaBatchId, cpDocumentName,
            cpResponseGenerated, errorMessage);

        // Populate the dao
        XhbPddaMessageDao dao = new XhbPddaMessageDao();
        dao.setCourtId(courtId);
        dao.setCourtRoomId(courtRoomId);
        dao.setPddaMessageTypeId(pddaMessageTypeId);
        dao.setPddaMessageDataId(pddaMessageDataId);
        dao.setPddaBatchId(pddaBatchId);
        dao.setTimeSent(null);
        dao.setCpDocumentName(cpDocumentName);
        dao.setCpDocumentStatus(errorMessage != null ? CpDocumentStatus.INVALID.status
            : CpDocumentStatus.VALID_NOT_PROCESSED.status);
        dao.setCpResponseGenerated(cpResponseGenerated);
        dao.setErrorMessage(errorMessage);

        // Create the record
        pddaMessageHelper.savePddaMessage(dao);
    }

    public static Optional<XhbRefPddaMessageTypeDao> createMessageType(
        final PddaMessageHelper pddaMessageHelper, final String messageType,
        final LocalDateTime batchOpenedDatetime) {
        LOG.debug("createMessageType({},{},{})", pddaMessageHelper, messageType,
            batchOpenedDatetime);

        // Populate the dao
        XhbRefPddaMessageTypeDao dao = new XhbRefPddaMessageTypeDao();
        dao.setPddaMessageType(messageType);
        dao.setPddaMessageDescription(DATETIMEFORMAT.format(batchOpenedDatetime));

        // Create the record
        return pddaMessageHelper.savePddaMessageType(dao);
    }

    public static Optional<XhbClobDao> createClob(XhbClobRepository clobRepository,
        String clobData) {
        if (clobData != null) {
            LOG.debug("createClob({},{})", clobRepository, clobData);
            XhbClobDao dao = new XhbClobDao();
            dao.setClobData(clobData);
            return clobRepository.update(dao);
        }
        return Optional.empty();
    }

    public static void updatePddaMessageRecords(PddaMessageHelper pddaMessageHelper,
        List<XhbPddaMessageDao> messages, String userDisplayName) {
        LOG.debug("updatePddaMessageRecords({},{},{})", pddaMessageHelper, messages,
            userDisplayName);
        // Update CP_RESPONSE_GENERATED = 'Y' for this record now that it has a response
        if (!messages.isEmpty()) {
            for (XhbPddaMessageDao dao : messages) {
                dao.setCpResponseGenerated(YES);
                pddaMessageHelper.updatePddaMessage(dao, userDisplayName);
            }
        }
    }

    public static void updateCppStagingInboundRecords(
        CppStagingInboundHelper cppStagingInboundHelper, List<XhbCppStagingInboundDao> cppMessages,
        String userDisplayName) {
        LOG.debug("updateCppStagingInboundRecords({},{},{})", cppStagingInboundHelper, cppMessages,
            userDisplayName);
        // Update ACKNOWLEDGMENT_STATUS = 'AS' for this record now that it has a response
        if (!cppMessages.isEmpty()) {
            for (XhbCppStagingInboundDao dao : cppMessages) {
                dao.setAcknowledgmentStatus(ACKNOWLEDGE_SUCCESS);
                cppStagingInboundHelper.updateCppStagingInbound(dao, userDisplayName);
            }
        }
    }
}
