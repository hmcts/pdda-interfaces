package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

/**
 * This class wraps the stored procedure that provides the data for the summary by name document.
 * Even though the instance is not thread safe, you can still cache this in a session bean as the
 * container serializes invocations on a given session bean instance. In short, stateless session
 * bean instances are pooled and wouldn't allow concurrent invocations.

 * @author pznwc5
 */
@SuppressWarnings("PMD.ExcessiveParameterList")
public class SummaryByNameUnassignedCasesQuery extends SummaryByNameQuery {

    /**
     * Constructor compiles the query.
     */
    public SummaryByNameUnassignedCasesQuery(EntityManager entityManager) {
        super(entityManager);
    }

    public SummaryByNameUnassignedCasesQuery(EntityManager entityManager,
        XhbCaseRepository xhbCaseRepository, XhbCaseReferenceRepository xhbCaseReferenceRepository,
        XhbHearingListRepository xhbHearingListRepository,
        XhbSittingRepository xhbSittingRepository,
        XhbScheduledHearingRepository xhbScheduledHearingRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository,
        XhbSchedHearingDefendantRepository xhbSchedHearingDefendantRepository,
        XhbHearingRepository xhbHearingRepository,
        XhbDefendantOnCaseRepository xhbDefendantOnCaseRepository,
        XhbDefendantRepository xhbDefendantRepository,
        XhbCourtLogEntryRepository xhbCourtLogEntryRepository,
        XhbRefHearingTypeRepository xhbRefHearingTypeRepository) {
        super(entityManager, xhbCaseRepository, xhbCaseReferenceRepository,
            xhbHearingListRepository, xhbSittingRepository, xhbScheduledHearingRepository,
            xhbCourtSiteRepository, xhbCourtRoomRepository, xhbSchedHearingDefendantRepository,
            xhbHearingRepository, xhbDefendantOnCaseRepository, xhbDefendantRepository,
            xhbCourtLogEntryRepository, xhbRefHearingTypeRepository);
    }

    @Override
    protected boolean isFloatingIncluded() {
        return true;
    }
}
