package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.ActiveCasesInRoomQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("static-access")
@ExtendWith(EasyMockExtension.class)
class PublicDisplayActivationHelperTest {

    private static final String NOT_TRUE = "Result is Not True";
    private static final String NOT_FALSE = "Result is Not False";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private ActiveCasesInRoomQuery mockActiveCasesInRoomQuery;

    @TestSubject
    private final PublicDisplayActivationHelper classUnderTest =
        new PublicDisplayActivationHelper();

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new PublicDisplayActivationHelper();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testIsPublicDisplayActiveWrapper() {
        // Run
        boolean result = classUnderTest.isPublicDisplayActive(0, mockEntityManager);

        // Checks
        assertFalse(result, NOT_FALSE);
    }
    
    @Test
    void testIsPublicDisplayActive() {
        // Setup
        XhbScheduledHearingDao xhbScheduledHearingDao =
            DummyHearingUtil.getXhbScheduledHearingDao();
        EasyMock.expect(mockXhbScheduledHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbScheduledHearingDao));
        EasyMock.replay(mockXhbScheduledHearingRepository);

        // Run
        boolean result = classUnderTest.isPublicDisplayActive(0, mockXhbScheduledHearingRepository);

        // Checks
        EasyMock.verify(mockXhbScheduledHearingRepository);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testIsPublicDisplayActiveNotActive() {
        // Setup
        XhbScheduledHearingDao xhbScheduledHearingDao =
            DummyHearingUtil.getXhbScheduledHearingDao();
        xhbScheduledHearingDao.setIsCaseActive("N");
        EasyMock.expect(mockXhbScheduledHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbScheduledHearingDao));
        EasyMock.replay(mockXhbScheduledHearingRepository);

        // Run
        boolean result = classUnderTest.isPublicDisplayActive(0, mockXhbScheduledHearingRepository);

        // Checks
        EasyMock.verify(mockXhbScheduledHearingRepository);
        assertFalse(result, NOT_FALSE);
    }

    @Test
    void testActivatePublicDisplayWrapper() {
        // Setup
        boolean result = true;
        
        // Run
        classUnderTest.activatePublicDisplay(mockPublicDisplayNotifier, 0, new Date(), true, mockEntityManager);
        
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testActivatePublicDisplayActivate() {
        // Setup
        Optional<XhbScheduledHearingDao> xhbScheduledHearingDao =
            Optional.of(DummyHearingUtil.getXhbScheduledHearingDao());
        List<XhbCrLiveDisplayDao> xhbCrLiveDisplayDaos = new ArrayList<>();
        xhbCrLiveDisplayDaos.add(DummyDisplayUtil.getXhbCrLiveDisplayDao());
        XhbCourtRoomDao xhbCourtRoomDao = DummyCourtUtil.getXhbCourtRoomDao();
        xhbCourtRoomDao.setXhbCrLiveDisplays(xhbCrLiveDisplayDaos);
        xhbScheduledHearingDao.get().getXhbSitting().setXhbCourtRoom(xhbCourtRoomDao);
        List<Integer> shIds = new ArrayList<>();
        shIds.add(1);

        EasyMock.expect(mockXhbScheduledHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbScheduledHearingDao).times(2);
        EasyMock.expect(mockXhbScheduledHearingRepository.update(xhbScheduledHearingDao.get()))
            .andReturn(xhbScheduledHearingDao).times(2);
        EasyMock.expect(mockActiveCasesInRoomQuery.getData(EasyMock.isA(Integer.class),
            EasyMock.isA(Integer.class), EasyMock.isA(Integer.class))).andReturn(shIds);

        EasyMock.replay(mockXhbScheduledHearingRepository);
        EasyMock.replay(mockActiveCasesInRoomQuery);

        // Run
        final boolean result = true;
        classUnderTest.activatePublicDisplay(mockPublicDisplayNotifier, 0, new Date(), true,
            mockXhbScheduledHearingRepository, mockActiveCasesInRoomQuery);

        // Checks
        EasyMock.verify(mockXhbScheduledHearingRepository);
        EasyMock.verify(mockActiveCasesInRoomQuery);
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testActivatePublicDisplayDeactivate() {
        // Setup
        XhbScheduledHearingDao xhbScheduledHearingDao =
            DummyHearingUtil.getXhbScheduledHearingDao();
        List<XhbCrLiveDisplayDao> xhbCrLiveDisplayDaos = new ArrayList<>();
        xhbCrLiveDisplayDaos.add(DummyDisplayUtil.getXhbCrLiveDisplayDao());
        XhbCourtRoomDao xhbCourtRoomDao = DummyCourtUtil.getXhbCourtRoomDao();
        xhbCourtRoomDao.setXhbCrLiveDisplays(xhbCrLiveDisplayDaos);
        xhbScheduledHearingDao.getXhbSitting().setXhbCourtRoom(xhbCourtRoomDao);
        List<Integer> shIds = new ArrayList<>();
        shIds.add(1);

        for (int i = 0; i < 2; i++) {
            EasyMock
                .expect(mockXhbScheduledHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(xhbScheduledHearingDao));
            EasyMock.expect(mockXhbScheduledHearingRepository.update(xhbScheduledHearingDao))
                .andReturn(Optional.of(xhbScheduledHearingDao));
        }

        EasyMock.expect(mockActiveCasesInRoomQuery.getData(EasyMock.isA(Integer.class),
            EasyMock.isA(Integer.class), EasyMock.isA(Integer.class))).andReturn(shIds);

        EasyMock.replay(mockXhbScheduledHearingRepository);
        EasyMock.replay(mockActiveCasesInRoomQuery);

        // Run
        final boolean result = true;
        classUnderTest.activatePublicDisplay(mockPublicDisplayNotifier, 0, new Date(), false,
            mockXhbScheduledHearingRepository, mockActiveCasesInRoomQuery);

        // Checks
        EasyMock.verify(mockXhbScheduledHearingRepository);
        EasyMock.verify(mockActiveCasesInRoomQuery);
        assertTrue(result, NOT_TRUE);
    }
}
