package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: CourtDetailQueryCaseHiddenConditionsTest.
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
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class CourtDetailQueryCaseHiddenConditionsTest extends CourtDetailQueryTest {

    @Test
    void testGetDataNoListSuccess() {
        boolean result = testGetDataNoList(getXhbHearingListDaoList(), getXhbSittingDaoList(),
            getXhbScheduledHearingDaoList(), getXhbSchedHearingDefendantDaoList(),
            Optional.of(DummyHearingUtil.getXhbHearingDao()), false, false, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataCaseHidden() {
        boolean result = testGetDataNoList(getXhbHearingListDaoList(), getXhbSittingDaoList(),
            getXhbScheduledHearingDaoList(), getXhbSchedHearingDefendantDaoList(),
            Optional.of(DummyHearingUtil.getXhbHearingDao()), true, false, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataDefOnCasePublicDispHide() {
        boolean result = testGetDataNoList(getXhbHearingListDaoList(), getXhbSittingDaoList(),
            getXhbScheduledHearingDaoList(), getXhbSchedHearingDefendantDaoList(),
            Optional.of(DummyHearingUtil.getXhbHearingDao()), false, true, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataDefPublicDispHide() {
        boolean result = testGetDataNoList(getXhbHearingListDaoList(), getXhbSittingDaoList(),
            getXhbScheduledHearingDaoList(), getXhbSchedHearingDefendantDaoList(),
            Optional.of(DummyHearingUtil.getXhbHearingDao()), false, false, true);
        assertTrue(result, TRUE);
    }

    List<XhbHearingListDao> getXhbHearingListDaoList() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        return xhbHearingListDaoList;
    }

    List<XhbSittingDao> getXhbSittingDaoList() {
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        return xhbSittingDaoList;
    }

    List<XhbScheduledHearingDao> getXhbScheduledHearingDaoList() {
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList = new ArrayList<>();
        xhbScheduledHearingDaoList.add(DummyHearingUtil.getXhbScheduledHearingDao());
        return xhbScheduledHearingDaoList;
    }

    List<XhbSchedHearingDefendantDao> getXhbSchedHearingDefendantDaoList() {
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList = new ArrayList<>();
        xhbSchedHearingDefendantDaoList.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        return xhbSchedHearingDefendantDaoList;
    }
}
