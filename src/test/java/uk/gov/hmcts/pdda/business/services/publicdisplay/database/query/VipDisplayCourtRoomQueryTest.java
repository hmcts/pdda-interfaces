package uk.gov.hmcts.pdda.business.services.publicdisplay.database.query;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaycourtroom.XhbDisplayCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaycourtroom.XhbDisplayCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: VipDisplayCourtRoomQueryTest Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD.TooManyMethods")
class VipDisplayCourtRoomQueryTest {

    private static final String TRUE = "Result is not True";

    @Mock
    protected EntityManager mockEntityManager;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

    @Mock
    private XhbDisplayCourtRoomRepository mockXhbDisplayCourtRoomRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @TestSubject
    protected VipDisplayCourtRoomQuery classUnderTest =
        new VipDisplayCourtRoomQuery(mockEntityManager, mockXhbDisplayRepository, mockXhbDisplayLocationRepository,
            mockXhbDisplayCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @BeforeEach
    void setupEntityManagerExpectations() {
        EasyMock.expect(mockEntityManager.isOpen()).andStubReturn(true);
        EasyMock.replay(mockEntityManager);
    }


    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new VipDisplayCourtRoomQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>(), Optional.empty(), new ArrayList<>(), new ArrayList<>(),
            Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testIsShowUnassignedCases() {
        assertFalse(classUnderTest.isShowUnassignedCases(), "Result is not False");
    }

    @Test
    void testGetDataNoListNoCourtSite() {
        List<XhbDisplayLocationDao> xhbDisplayLocationDaoList = new ArrayList<>();
        xhbDisplayLocationDaoList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        boolean result = testGetDataNoList(xhbDisplayLocationDaoList, Optional.empty(), new ArrayList<>(),
            new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoDisplay() {
        List<XhbDisplayLocationDao> xhbDisplayLocationDaoList = new ArrayList<>();
        xhbDisplayLocationDaoList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        boolean result = testGetDataNoList(xhbDisplayLocationDaoList, Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            new ArrayList<>(), new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoDisplayCourtRoom() {
        List<XhbDisplayLocationDao> xhbDisplayLocationDaoList = new ArrayList<>();
        xhbDisplayLocationDaoList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        List<XhbDisplayDao> xhbDisplayDaoList = new ArrayList<>();
        xhbDisplayDaoList.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        boolean result = testGetDataNoList(xhbDisplayLocationDaoList, Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            xhbDisplayDaoList, new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoCourtRoom() {
        List<XhbDisplayLocationDao> xhbDisplayLocationDaoList = new ArrayList<>();
        xhbDisplayLocationDaoList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        List<XhbDisplayDao> xhbDisplayDaoList = new ArrayList<>();
        xhbDisplayDaoList.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        List<XhbDisplayCourtRoomDao> xhbDisplayCourtRoomDaoList = new ArrayList<>();
        xhbDisplayCourtRoomDaoList.add(DummyPublicDisplayUtil.getXhbDisplayCourtRoomDao());
        boolean result = testGetDataNoList(xhbDisplayLocationDaoList, Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            xhbDisplayDaoList, xhbDisplayCourtRoomDaoList, Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListSuccess() {
        List<XhbDisplayLocationDao> xhbDisplayLocationDaoList = new ArrayList<>();
        xhbDisplayLocationDaoList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        List<XhbDisplayDao> xhbDisplayDaoList = new ArrayList<>();
        xhbDisplayDaoList.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        List<XhbDisplayCourtRoomDao> xhbDisplayCourtRoomDaoList = new ArrayList<>();
        xhbDisplayCourtRoomDaoList.add(DummyPublicDisplayUtil.getXhbDisplayCourtRoomDao());
        boolean result = testGetDataNoList(xhbDisplayLocationDaoList, Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            xhbDisplayDaoList, xhbDisplayCourtRoomDaoList, Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        assertTrue(result, TRUE);
    }

    private boolean testGetDataNoList(List<XhbDisplayLocationDao> xhbDisplayLocationDaoList,
        Optional<XhbCourtSiteDao> xhbCourtSiteDao, List<XhbDisplayDao> xhbDisplayDaoList,
        List<XhbDisplayCourtRoomDao> xhbDisplayCourtRoomDaoList, Optional<XhbCourtRoomDao> xhbCourtRoomDao) {
        // Setup
        final Integer courtSiteId = 81;
        List<AbstractRepository<?>> replayArray = new ArrayList<>();

        // Expects
        boolean abortExpects;
        EasyMock.expect(mockXhbDisplayLocationRepository.findByVipCourtSite(EasyMock.isA(Integer.class)))
            .andReturn(xhbDisplayLocationDaoList);
        replayArray.add(mockXhbDisplayLocationRepository);
        abortExpects = xhbDisplayLocationDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCourtSiteDao);
            replayArray.add(mockXhbCourtSiteRepository);
            abortExpects = xhbCourtSiteDao.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbDisplayRepository.findByDisplayLocationId(EasyMock.isA(Integer.class)))
                .andReturn(xhbDisplayDaoList);
            replayArray.add(mockXhbDisplayRepository);
            abortExpects = xhbDisplayDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbDisplayCourtRoomRepository.findByDisplayId(EasyMock.isA(Integer.class)))
                .andReturn(xhbDisplayCourtRoomDaoList);
            replayArray.add(mockXhbDisplayCourtRoomRepository);
            abortExpects = xhbDisplayCourtRoomDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCourtRoomDao);
            replayArray.add(mockXhbCourtRoomRepository);
        }

        // Replays
        for (AbstractRepository<?> repository : replayArray) {
            EasyMock.replay(repository);
        }

        // Run
        classUnderTest.getData(courtSiteId);

        // Checks
        for (AbstractRepository<?> repository : replayArray) {
            EasyMock.verify(repository);
        }
        return true;
    }

}
