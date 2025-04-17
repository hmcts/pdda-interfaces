package uk.gov.hmcts.pdda.business.services.publicdisplay.database.query;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>
 * Title: Active Cases in Court Room query.
 * </p>
 * <p>
 * Description: This runs the stored procedure that for a given list, it finds all the active cases
 * in the court room except for the case supplied (identified by its scheduled hearing id). I would
 * expect that in most cases this will only be zero or one record.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: ActiveCasesInRoomQuery.java,v 1.3 2005/11/17 10:55:48 bzjrnl Exp $
 */
@SuppressWarnings("PMD.NullAssignment")
public class ActiveCasesInRoomQuery extends AbstractControllerBean {
    /** Logger object. */
    private static final Logger LOG = LoggerFactory.getLogger(ActiveCasesInRoomQuery.class);

    private EntityManager entityManager;

    private XhbScheduledHearingRepository xhbScheduledHearingRepository;

    public ActiveCasesInRoomQuery(final EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    public ActiveCasesInRoomQuery(final EntityManager entityManager,
        XhbScheduledHearingRepository xhbScheduledHearingRepository) {
        this(entityManager);
        this.xhbScheduledHearingRepository = xhbScheduledHearingRepository;
    }

    @Override
    protected void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbScheduledHearingRepository = null;
    }

    /**
     * Returns an array of CourtListValue.
     * 
     * @param listId Id
     * @param courtRoomId room id for which the data is required
     * @param scheduledHearingId Scheduled Hearing id
     * 
     * @return Summary by name data for the specified court rooms
     */
    public Collection<Integer> getData(Integer listId, Integer courtRoomId,
        Integer scheduledHearingId) {
        LOG.debug("getData({},{},{})", listId, courtRoomId, scheduledHearingId);
        List<Integer> results = new ArrayList<>();
        List<XhbScheduledHearingDao> daos = getXhbScheduledHearingRepository()
            .findActiveCasesInRoom(listId, courtRoomId, scheduledHearingId);
        if (!daos.isEmpty()) {
            for (XhbScheduledHearingDao dao : daos) {
                LOG.debug("Found scheduledHearingId = {}", dao.getScheduledHearingId());
                results.add(dao.getScheduledHearingId());
            }
        }
        return results;
    }

    private XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
        if (xhbScheduledHearingRepository == null || !isEntityManagerActive()) {
            xhbScheduledHearingRepository = new XhbScheduledHearingRepository(entityManager);
        }
        return xhbScheduledHearingRepository;
    }
}
