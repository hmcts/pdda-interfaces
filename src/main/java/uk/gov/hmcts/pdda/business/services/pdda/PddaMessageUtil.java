package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ActivateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.AddCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CaseStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.HearingStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.UpdateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.pdda.PddaHearingProgressEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * PddaMessageUtil.
 **/
@SuppressWarnings("PMD")
public final class PddaMessageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PddaMessageUtil.class);

    private static final DateTimeFormatter DATETIMEFORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String YES = "Y";
    private static final String ACKNOWLEDGE_SUCCESS = "AS";

    private PddaMessageUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void createMessage(final PddaMessageHelper pddaMessageHelper,
        final Integer courtId, final Integer courtRoomId, final Integer pddaMessageTypeId,
        final Long pddaMessageDataId, final Integer pddaBatchId, final String cpDocumentName,
        final String cpResponseGenerated, final String errorMessage) {
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

    private static CourtRoomIdentifier remapCourtRoomIdentifier(CourtRoomIdentifier current,
        XhbCourtRepository courtRepository, XhbCourtRoomRepository courtRoomRepository,
        XhbCourtSiteRepository courtSiteRepository) {

        String courtName = current.getCourtName();
        Integer courtRoomNo = current.getCourtRoomNo();
        Integer newCourtId = getRealCourtId(courtName, courtRepository);
        Integer newCourtRoomId =
            getRealCourtRoomId(newCourtId, courtRoomNo, courtRoomRepository, courtSiteRepository);

        CourtRoomIdentifier updated = new CourtRoomIdentifier(newCourtId, newCourtRoomId, courtName, courtRoomNo);

        // ---- Preserve publicNotices (if present) ----
        DisplayablePublicNoticeValue[] notices = current.getPublicNotices();
        if (notices != null) {
            // defensive copy so we don't share mutable array instance
            DisplayablePublicNoticeValue[] copy = new DisplayablePublicNoticeValue[notices.length];
            System.arraycopy(notices, 0, copy, 0, notices.length);
            updated.setPublicNotices(copy);
        }

        return updated;
    }

    /** Generic utility that uses method references to avoid casts/interfaces. */
    private static void remapUsing(Supplier<CourtRoomIdentifier> getter,
        Consumer<CourtRoomIdentifier> setter, XhbCourtRepository courtRepository,
        XhbCourtRoomRepository courtRoomRepository, XhbCourtSiteRepository courtSiteRepository) {

        CourtRoomIdentifier updated = remapCourtRoomIdentifier(getter.get(), courtRepository,
            courtRoomRepository, courtSiteRepository);
        setter.accept(updated);
    }


    /**
     * Get the court name from the event.
     * @return event with details added
     */
    public static PublicDisplayEvent translatePublicDisplayEvent(PublicDisplayEvent event,
        XhbCourtRepository courtRepository, XhbCourtRoomRepository courtRoomRepository,
        XhbCourtSiteRepository courtSiteRepository) {

        if (event instanceof ActivateCaseEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for ActivateCaseEvent for case {}", event,
                newEvent.getCaseChangeInformation());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof AddCaseEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for AddCaseEvent for case {}", event,
                newEvent.getCaseChangeInformation());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof CaseStatusEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for caseStatusEvent for hearing {}", event,
                newEvent.getCourtRoomIdentifier());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof ConfigurationChangeEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for PublicNoticeEvent for hearing {}", event,
                newEvent);
            String courtName = newEvent.getChange().getCourtName();
            Integer newCourtId = getRealCourtId(courtName, courtRepository);
            newEvent.setConfigurationChange(new CourtConfigurationChange(newCourtId, courtName, true));

        } else if (event instanceof HearingStatusEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for HearingStatusEvent for hearing {}",
                event, newEvent.getCourtRoomIdentifier());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof MoveCaseEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for MoveCaseEvent for hearing {}", event,
                newEvent.getCourtRoomIdentifier());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof PublicNoticeEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for PublicNoticeEvent for hearing {}", event,
                newEvent.getCourtRoomIdentifier());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof UpdateCaseEvent newEvent) {
            // (Nit: message said "DeactivateCaseEvent" before—assuming that was a copy/paste.)
            LOG.debug("translatePublicDisplayEvent({}) for UpdateCaseEvent for case {}", event,
                newEvent.getCaseChangeInformation());
            remapUsing(newEvent::getCourtRoomIdentifier, newEvent::setCourtRoomIdentifier, courtRepository,
                courtRoomRepository, courtSiteRepository);

        } else if (event instanceof PddaHearingProgressEvent newEvent) {
            LOG.debug("translatePublicDisplayEvent({}) for PddaHearingProgressEvent for court {}", event,
                newEvent.getCourtName());
            // Set the courtId from the pdda database using the courtName from xhibit
            List<XhbCourtDao> courtDao = courtRepository.findByCourtNameValueSafe(newEvent.getCourtName());
            newEvent.setCourtId(courtDao.get(0).getCourtId());
            LOG.debug("Court name from XHIBIT: {} mapped to court id {}, on PDDA",
                newEvent.getCourtName(), newEvent.getCourtId());
        } else {
            LOG.debug("translatePublicDisplayEvent({}) for unknown event type", event);
        }
        return event;
    }

    /**
     * Get the court room number from the new court id and court room number.
     * @param newCourtId new court id
     * @param courtRoomNo court room number
     * @param courtRoomRepository court room repository
     * @param courtSiteRepository court site repository
     * @return court room id
     */
    private static Integer getRealCourtRoomId(Integer newCourtId, Integer courtRoomNo,
        XhbCourtRoomRepository courtRoomRepository, XhbCourtSiteRepository courtSiteRepository) {

        // Get the correct court room id, but first need the court site id
        List<XhbCourtSiteDao> courtSites = courtSiteRepository.findByCourtIdSafe(newCourtId);
        if (!courtSites.isEmpty()) {
            XhbCourtSiteDao courtSite = courtSites.get(0);
            Integer courtSiteId = courtSite.getCourtSiteId();
            Optional<XhbCourtRoomDao> courtRoom =
                courtRoomRepository.findByCourtRoomNoSafe(courtSiteId, courtRoomNo);
            if (!courtRoom.isEmpty()) {
                XhbCourtRoomDao xhbCourtRoom = courtRoom.get();
                return xhbCourtRoom.getCourtRoomId();
            } else {
                LOG.warn("No court room found for court id {} and court room no {}", newCourtId,
                    courtRoomNo);
                return null;
            }
        } else {
            LOG.warn("No court site found for court id {} and court room no {}", newCourtId,
                courtRoomNo);
            return null;
        }
    }

    /**
     * Get the real courtid for the court name.
     * @param courtName court name
     * @param courtRepository court repository
     * @return court id
     */
    private static Integer getRealCourtId(String courtName, XhbCourtRepository courtRepository) {
        List<XhbCourtDao> courts = courtRepository.findByCourtNameValueSafe(courtName);
        if (!courts.isEmpty()) {
            XhbCourtDao court = courts.get(0);
            return court.getCourtId();
        } else {
            LOG.warn("No court found for court name {}", courtName);
        }
        return null;

    }
}
