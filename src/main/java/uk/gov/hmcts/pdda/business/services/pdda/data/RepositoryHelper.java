package uk.gov.hmcts.pdda.business.services.pdda.data;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

/**
 * <p>
 * Title: RepositoryHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList"})
public class RepositoryHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryHelper.class);

    private EntityManager entityManager;
    private XhbCourtSiteRepository xhbCourtSiteRepository;
    private XhbCourtRoomRepository xhbCourtRoomRepository;
    private XhbHearingListRepository xhbHearingListRepository;
    private XhbSittingRepository xhbSittingRepository;
    private XhbCaseRepository xhbCaseRepository;
    private XhbDefendantOnCaseRepository xhbDefendantOnCaseRepository;
    private XhbDefendantRepository xhbDefendantRepository;
    private XhbHearingRepository xhbHearingRepository;
    private XhbScheduledHearingRepository xhbScheduledHearingRepository;
    private XhbSchedHearingDefendantRepository xhbSchedHearingDefendantRepository;
    private XhbCrLiveDisplayRepository xhbCrLiveDisplayRepository;

    public RepositoryHelper() {
        // Default constructor.
    }

    // JUnit constructor
    public RepositoryHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbCourtSiteRepository = null;
        xhbCourtRoomRepository = null;
        xhbHearingListRepository = null;
        xhbSittingRepository = null;
        xhbCaseRepository = null;
        xhbDefendantRepository = null;
        xhbDefendantOnCaseRepository = null;
        xhbHearingRepository = null;
        xhbScheduledHearingRepository = null;
        xhbSchedHearingDefendantRepository = null;
        xhbCrLiveDisplayRepository = null;
    }

    private boolean isEntityManagerActive() {
        return EntityManagerUtil.isEntityManagerActive(entityManager);
    }

    protected EntityManager getEntityManager() {
        if (!isEntityManagerActive()) {
            clearRepositories();
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }

    public XhbCourtSiteRepository getXhbCourtSiteRepository() {
        if (xhbCourtSiteRepository == null || !isEntityManagerActive()) {
            xhbCourtSiteRepository = new XhbCourtSiteRepository(getEntityManager());
        }
        return xhbCourtSiteRepository;
    }

    public XhbCourtRoomRepository getXhbCourtRoomRepository() {
        if (xhbCourtRoomRepository == null || !isEntityManagerActive()) {
            xhbCourtRoomRepository = new XhbCourtRoomRepository(getEntityManager());
        }
        return xhbCourtRoomRepository;
    }

    public XhbHearingListRepository getXhbHearingListRepository() {
        if (xhbHearingListRepository == null || !isEntityManagerActive()) {
            xhbHearingListRepository = new XhbHearingListRepository(getEntityManager());
        }
        return xhbHearingListRepository;
    }

    public XhbSittingRepository getXhbSittingRepository() {
        if (xhbSittingRepository == null || !isEntityManagerActive()) {
            xhbSittingRepository = new XhbSittingRepository(getEntityManager());
        }
        return xhbSittingRepository;
    }

    public XhbCaseRepository getXhbCaseRepository() {
        if (xhbCaseRepository == null || !isEntityManagerActive()) {
            xhbCaseRepository = new XhbCaseRepository(getEntityManager());
        }
        return xhbCaseRepository;
    }

    public XhbDefendantOnCaseRepository getXhbDefendantOnCaseRepository() {
        if (xhbDefendantOnCaseRepository == null || !isEntityManagerActive()) {
            xhbDefendantOnCaseRepository = new XhbDefendantOnCaseRepository(getEntityManager());
        }
        return xhbDefendantOnCaseRepository;
    }

    public XhbDefendantRepository getXhbDefendantRepository() {
        if (xhbDefendantRepository == null || !isEntityManagerActive()) {
            xhbDefendantRepository = new XhbDefendantRepository(getEntityManager());
        }
        return xhbDefendantRepository;
    }

    public XhbHearingRepository getXhbHearingRepository() {
        if (xhbHearingRepository == null || !isEntityManagerActive()) {
            xhbHearingRepository = new XhbHearingRepository(getEntityManager());
        }
        return xhbHearingRepository;
    }

    public XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
        if (xhbScheduledHearingRepository == null || !isEntityManagerActive()) {
            xhbScheduledHearingRepository = new XhbScheduledHearingRepository(getEntityManager());
        }
        return xhbScheduledHearingRepository;
    }

    public XhbSchedHearingDefendantRepository getXhbSchedHearingDefendantRepository() {
        if (xhbSchedHearingDefendantRepository == null || !isEntityManagerActive()) {
            xhbSchedHearingDefendantRepository =
                new XhbSchedHearingDefendantRepository(getEntityManager());
        }
        return xhbSchedHearingDefendantRepository;
    }

    public XhbCrLiveDisplayRepository getXhbCrLiveDisplayRepository() {
        if (xhbCrLiveDisplayRepository == null || !isEntityManagerActive()) {
            xhbCrLiveDisplayRepository = new XhbCrLiveDisplayRepository(getEntityManager());
        }
        return xhbCrLiveDisplayRepository;
    }

}