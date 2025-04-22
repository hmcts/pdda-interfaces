package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc.XhbCourtLogEventDescDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc.XhbCourtLogEventDescRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("PMD.NullAssignment")
public class PublicDisplayQueryLogEntry extends AbstractControllerBean {

    private static final Logger LOG = LoggerFactory.getLogger(PublicDisplayQueryLogEntry.class);
    
    private static final Integer SHORT_ADJOURN = 30_100;
    private static final Integer LONG_ADJOURN = 30_200;
    private static final Integer CASE_CLOSED = 30_300;
    private static final Integer RESUME = 10_500;
    private final Integer[] validEventTypes = {SHORT_ADJOURN, LONG_ADJOURN, CASE_CLOSED, RESUME};

    private XhbCourtLogEntryRepository xhbCourtLogEntryRepository;
    private XhbCourtLogEventDescRepository xhbCourtLogEventDescRepository;
    protected XhbCaseRepository xhbCaseRepository;
    protected XhbCourtSiteRepository xhbCourtSiteRepository;
    protected XhbCourtRoomRepository xhbCourtRoomRepository;

    protected PublicDisplayQueryLogEntry(EntityManager entityManager) {
        super(entityManager);
    }

    protected PublicDisplayQueryLogEntry(EntityManager entityManager,
        XhbCourtLogEntryRepository xhbCourtLogEntryRepository,
        XhbCourtLogEventDescRepository xhbCourtLogEventDescRepository) {
        this(entityManager);
        this.xhbCourtLogEntryRepository = xhbCourtLogEntryRepository;
        this.xhbCourtLogEventDescRepository = xhbCourtLogEventDescRepository;
    }

    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        xhbCourtLogEntryRepository = null;
        xhbCourtLogEventDescRepository = null;
        xhbCaseRepository = null;
        xhbCourtSiteRepository = null;
        xhbCourtRoomRepository = null;
    }

    protected void populateEventData(PublicDisplayValue result, Integer caseId) {
        LOG.debug("populateEventData({},{})", result, caseId);
        List<XhbCourtLogEntryDao> courtLogEntryDaos = getXhbCourtLogEntryRepository().findByCaseId(caseId);
        if (!courtLogEntryDaos.isEmpty()) {
            XhbCourtLogEntryDao courtLogEntryDao = getLogEntry(courtLogEntryDaos);
            if (courtLogEntryDao != null) {
                result.setEvent(
                    (BranchEventXmlNode) EventXmlNodeHelper.buildEventNode(courtLogEntryDao.getLogEntryXml()));
                result.setEventTime(courtLogEntryDao.getDateTime());
            }
        }
    }

    private XhbCourtLogEntryDao getLogEntry(List<XhbCourtLogEntryDao> courtLogEntryDaos) {
        XhbCourtLogEntryDao firstRec = null;
        XhbCourtLogEntryDao caseClosedRec = null;
        Integer recNo = 0;
        for (XhbCourtLogEntryDao courtLogEntryDao : courtLogEntryDaos) {
            // Get the event type
            Integer eventType = getCourtLogEventType(courtLogEntryDao);

            if (isValidEventType(eventType)) {
                // Increment the valid event recNo
                recNo++;

                if (recNo == 1 && !RESUME.equals(eventType)) {
                    // Use the first record if its not a RESUME and there are no higher priority
                    // event logs
                    firstRec = courtLogEntryDao;
                } else if (LONG_ADJOURN.equals(eventType)) {
                    // Use the first caseClosedRec, if there is one, otherwise use this record
                    return caseClosedRec != null ? caseClosedRec : courtLogEntryDao;
                } else if (CASE_CLOSED.equals(eventType) && caseClosedRec == null) {
                    // Store the first caseClosedRec
                    caseClosedRec = courtLogEntryDao;
                }
            }
        }
        return firstRec;
    }
    
    private boolean isValidEventType(Integer eventType) {
        return eventType != null && Arrays.asList(validEventTypes).contains(eventType);
    }

    private Integer getCourtLogEventType(XhbCourtLogEntryDao courtLogEntryDao) {
        Optional<XhbCourtLogEventDescDao> courtLogEventDescDao =
            getXhbCourtLogEventDescRepository().findByIdSafe(courtLogEntryDao.getEventDescId());
        return courtLogEventDescDao.isPresent() ? courtLogEventDescDao.get().getEventType() : null;
    }

    private XhbCourtLogEntryRepository getXhbCourtLogEntryRepository() {
        if (xhbCourtLogEntryRepository == null || !isEntityManagerActive()) {
            xhbCourtLogEntryRepository = new XhbCourtLogEntryRepository(getEntityManager());
        }
        return xhbCourtLogEntryRepository;
    }

    private XhbCourtLogEventDescRepository getXhbCourtLogEventDescRepository() {
        if (xhbCourtLogEventDescRepository == null || !isEntityManagerActive()) {
            xhbCourtLogEventDescRepository = new XhbCourtLogEventDescRepository(getEntityManager());
        }
        return xhbCourtLogEventDescRepository;
    }

    protected final XhbCaseRepository getXhbCaseRepository() {
        if (xhbCaseRepository == null || !isEntityManagerActive()) {
            xhbCaseRepository = new XhbCaseRepository(getEntityManager());
        }
        return xhbCaseRepository;
    }

    protected XhbCourtSiteRepository getXhbCourtSiteRepository() {
        if (xhbCourtSiteRepository == null || !isEntityManagerActive()) {
            xhbCourtSiteRepository = new XhbCourtSiteRepository(getEntityManager());
        }
        return xhbCourtSiteRepository;
    }

    protected XhbCourtRoomRepository getXhbCourtRoomRepository() {
        if (xhbCourtRoomRepository == null || !isEntityManagerActive()) {
            xhbCourtRoomRepository = new XhbCourtRoomRepository(getEntityManager());
        }
        return xhbCourtRoomRepository;
    }
}
