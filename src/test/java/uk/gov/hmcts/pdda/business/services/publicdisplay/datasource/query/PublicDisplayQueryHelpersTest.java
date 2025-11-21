package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for PublicDisplayQueryHelpers.
 */
@SuppressWarnings("PMD")
class PublicDisplayQueryHelpersTest {

    @Test
    void pickBestByHearing_prefersRowWithHearingProgress() {
        // Prepare two scheduled hearing DAOs with the same hearingId
        XhbScheduledHearingDao first = DummyHearingUtil.getXhbScheduledHearingDao();
        XhbScheduledHearingDao second = DummyHearingUtil.getXhbScheduledHearingDao();

        // Ensure same hearingId
        int hearingId = 12345;
        first.setHearingId(hearingId);
        second.setHearingId(hearingId);

        // First has null progress, second has non-null progress -> second should be chosen
        first.setHearingProgress(null);
        second.setHearingProgress(5);

        List<XhbScheduledHearingDao> list = new ArrayList<>();
        // preserve insertion order: put first then second
        list.add(first);
        list.add(second);

        Map<Integer, XhbScheduledHearingDao> best = PublicDisplayQueryHelpers.pickBestByHearing(list);

        assertNotNull(best, "Result map must not be null");
        assertEquals(1, best.size(), "Should contain one entry for the hearingId");
        XhbScheduledHearingDao chosen = best.get(hearingId);
        assertNotNull(chosen, "Chosen scheduled hearing must not be null");
        assertEquals(Integer.valueOf(5), chosen.getHearingProgress(),
            "Should pick the row with non-null hearingProgress");
    }

    @Test
    void chooseOneDefendant_prefersNonObsOverObs() {
        // Create two sched hearing defendant DAOs
        XhbSchedHearingDefendantDao shd1 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        XhbSchedHearingDefendantDao shd2 = DummyHearingUtil.getXhbSchedHearingDefendantDao();

        // assign distinct defendantOnCaseIds
        shd1.setDefendantOnCaseId(1);
        shd2.setDefendantOnCaseId(2);

        List<XhbSchedHearingDefendantDao> list = new ArrayList<>();
        list.add(shd1);
        list.add(shd2);

        // prepare two XhbDefendantOnCaseDao instances: one OBS_IND = 'Y', other OBS_IND != 'Y'
        XhbDefendantOnCaseDao docObs = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        docObs.setObsInd("Y");
        docObs.setDefendantOnCaseId(1);

        XhbDefendantOnCaseDao docNonObs = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        docNonObs.setObsInd("N");
        docNonObs.setDefendantOnCaseId(2);

        // lambda to mimic repository findByIdSafe
        java.util.function.Function<Integer, Optional<XhbDefendantOnCaseDao>> findDefOnCase =
            id -> {
                if (id == 1) {
                    return Optional.of(docObs);
                } else if (id == 2) {
                    return Optional.of(docNonObs);
                }
                return Optional.empty();
            };

        XhbSchedHearingDefendantDao chosen = PublicDisplayQueryHelpers.chooseOneDefendant(list, findDefOnCase);

        assertNotNull(chosen, "Chosen defendant should not be null");
        assertEquals(2, chosen.getDefendantOnCaseId(), "Should choose the non-OBS defendant (id 2)");
    }

    @Test
    void buildDefendantNameForChosen_handlesNullChosenAndBuildsName() {
        // null chosen returns blank hidden DefendantName
        DefendantName blank = PublicDisplayQueryHelpers.buildDefendantNameForChosen(
            null,
            true,
            id -> Optional.empty(),
            id -> Optional.empty()
        );
        assertNotNull(blank);
        assertEquals("", blank.getName());
        assertTrue(blank.isHideInPublicDisplay(), "Blank defendant should be hidden");

        // Now test non-null chosen with repositories returning real DAOs
        XhbSchedHearingDefendantDao chosen = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        chosen.setDefendantOnCaseId(10);

        XhbDefendantOnCaseDao doc = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        doc.setObsInd("N"); // non-OBS
        doc.setPublicDisplayHide("N");
        doc.setDefendantOnCaseId(10);
        doc.setDefendantId(20);

        XhbDefendantDao def = DummyDefendantUtil.getXhbDefendantDao();
        def.setFirstName("John");
        def.setMiddleName("Q");
        def.setSurname("Public");
        def.setPublicDisplayHide("N");
        def.setDefendantId(20);

        java.util.function.Function<Integer, Optional<XhbDefendantOnCaseDao>> findDefOnCase =
            id -> id == 10 ? Optional.of(doc) : Optional.empty();

        java.util.function.Function<Integer, Optional<XhbDefendantDao>> findDef =
            id -> id == 20 ? Optional.of(def) : Optional.empty();

        DefendantName dn = PublicDisplayQueryHelpers.buildDefendantNameForChosen(chosen, false, findDefOnCase, findDef);

        assertNotNull(dn);
        assertEquals("John Q Public", dn.getName());
        assertFalse(dn.isHideInPublicDisplay(), "Defendant should not be hidden");
    }

    @Test
    void resolveHearingTypeDesc_returnsDescriptionWhenPresent() {
        XhbHearingDao hearing = DummyHearingUtil.getXhbHearingDao();
        hearing.setRefHearingTypeId(77);
        Optional<XhbHearingDao> hearingOpt = Optional.of(hearing);

        XhbRefHearingTypeDao ref = DummyHearingUtil.getXhbRefHearingTypeDao();
        ref.setHearingTypeDesc("My Hearing Type");
        ref.setRefHearingTypeId(77);

        java.util.function.Function<Integer, Optional<XhbRefHearingTypeDao>> findRef =
            id -> id == 77 ? Optional.of(ref) : Optional.empty();

        String desc = PublicDisplayQueryHelpers.resolveHearingTypeDesc(hearingOpt, findRef);
        assertEquals("My Hearing Type", desc);
    }
}
