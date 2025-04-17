package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc.XhbCourtLogEventDescRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods"})
public abstract class PublicDisplayQueryRepo extends PublicDisplayQueryLogEntry {

    protected XhbCaseReferenceRepository xhbCaseReferenceRepository;
    protected XhbDefendantRepository xhbDefendantRepository;
    protected XhbDefendantOnCaseRepository xhbDefendantOnCaseRepository;
    protected XhbHearingRepository xhbHearingRepository;
    protected XhbHearingListRepository xhbHearingListRepository;
    protected XhbRefJudgeRepository xhbRefJudgeRepository;
    protected XhbRefHearingTypeRepository xhbRefHearingTypeRepository;
    protected XhbSittingRepository xhbSittingRepository;
    protected XhbScheduledHearingRepository xhbScheduledHearingRepository;
    protected XhbSchedHearingDefendantRepository xhbSchedHearingDefendantRepository;

    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        xhbCaseReferenceRepository = null;
        xhbDefendantRepository = null;
        xhbDefendantOnCaseRepository = null;
        xhbHearingRepository = null;
        xhbHearingListRepository = null;
        xhbRefJudgeRepository = null;
        xhbRefHearingTypeRepository = null;
        xhbSittingRepository = null;
        xhbScheduledHearingRepository = null;
        xhbSchedHearingDefendantRepository = null;
    }

    protected PublicDisplayQueryRepo(EntityManager entityManager) {
        super(entityManager);
    }

    protected PublicDisplayQueryRepo(EntityManager entityManager,
        XhbCourtLogEntryRepository xhbCourtLogEntryRepository,
        XhbCourtLogEventDescRepository xhbCourtLogEventDescRepository) {
        super(entityManager, xhbCourtLogEntryRepository, xhbCourtLogEventDescRepository);
    }

    protected final XhbDefendantRepository getXhbDefendantRepository() {
        if (xhbDefendantRepository == null || !isEntityManagerActive()) {
            xhbDefendantRepository = new XhbDefendantRepository(getEntityManager());
        }
        return xhbDefendantRepository;
    }

    protected final XhbRefHearingTypeRepository getXhbRefHearingTypeRepository() {
        if (xhbRefHearingTypeRepository == null || !isEntityManagerActive()) {
            xhbRefHearingTypeRepository = new XhbRefHearingTypeRepository(getEntityManager());
        }
        return xhbRefHearingTypeRepository;
    }

    protected XhbHearingRepository getXhbHearingRepository() {
        if (xhbHearingRepository == null || !isEntityManagerActive()) {
            xhbHearingRepository = new XhbHearingRepository(getEntityManager());
        }
        return xhbHearingRepository;
    }

    protected XhbHearingListRepository getXhbHearingListRepository() {
        if (xhbHearingListRepository == null || !isEntityManagerActive()) {
            xhbHearingListRepository = new XhbHearingListRepository(getEntityManager());
        }
        return xhbHearingListRepository;
    }

    protected XhbRefJudgeRepository getXhbRefJudgeRepository() {
        if (xhbRefJudgeRepository == null || !isEntityManagerActive()) {
            xhbRefJudgeRepository = new XhbRefJudgeRepository(getEntityManager());
        }
        return xhbRefJudgeRepository;
    }

    protected XhbSittingRepository getXhbSittingRepository() {
        if (xhbSittingRepository == null || !isEntityManagerActive()) {
            xhbSittingRepository = new XhbSittingRepository(getEntityManager());
        }
        return xhbSittingRepository;
    }

    protected XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
        if (xhbScheduledHearingRepository == null || !isEntityManagerActive()) {
            xhbScheduledHearingRepository = new XhbScheduledHearingRepository(getEntityManager());
        }
        return xhbScheduledHearingRepository;
    }

    protected XhbSchedHearingDefendantRepository getXhbSchedHearingDefendantRepository() {
        if (xhbSchedHearingDefendantRepository == null || !isEntityManagerActive()) {
            xhbSchedHearingDefendantRepository =
                new XhbSchedHearingDefendantRepository(getEntityManager());
        }
        return xhbSchedHearingDefendantRepository;
    }

    protected XhbCaseReferenceRepository getXhbCaseReferenceRepository() {
        if (xhbCaseReferenceRepository == null || !isEntityManagerActive()) {
            xhbCaseReferenceRepository = new XhbCaseReferenceRepository(getEntityManager());
        }
        return xhbCaseReferenceRepository;
    }

    protected final XhbDefendantOnCaseRepository getXhbDefendantOnCaseRepository() {
        if (xhbDefendantOnCaseRepository == null || !isEntityManagerActive()) {
            xhbDefendantOnCaseRepository = new XhbDefendantOnCaseRepository(getEntityManager());
        }
        return xhbDefendantOnCaseRepository;
    }
}
