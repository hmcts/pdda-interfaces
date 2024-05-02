package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.CourtRoomNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.DisplayNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.RotationSetNotFoundCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings({"static-access", "PMD.TooManyMethods"})
@ExtendWith(EasyMockExtension.class)
class DisplayConfigurationHelperTest {

    private static final String NOT_TRUE = "Result is Not True";
    private static final String NULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbRotationSetsRepository mockXhbRotationSetsRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @TestSubject
    private final DisplayConfigurationHelper classUnderTest = new DisplayConfigurationHelper();

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new DisplayConfigurationHelper();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testGetDisplayConfiguration() {
        // Setup
        List<XhbCourtSiteDao> xhbCourtSites = new ArrayList<>();
        XhbDisplayDao displayDao = DummyPublicDisplayUtil.getXhbDisplayDao();
        EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(displayDao));
        EasyMock.expect(mockXhbRotationSetsRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbRotationSetsDao()));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtId(EasyMock.isA(Integer.class))).andReturn(xhbCourtSites);
        EasyMock.expect(mockXhbCourtRoomRepository.findByDisplayId(EasyMock.isA(Integer.class)))
            .andReturn(new ArrayList<>());
        EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class))).andReturn(Optional.empty());
        EasyMock.replay(mockXhbDisplayRepository);
        EasyMock.replay(mockXhbRotationSetsRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);
        EasyMock.replay(mockXhbCourtRoomRepository);
        // Run
        DisplayConfiguration result =
            classUnderTest.getDisplayConfiguration(0, mockXhbDisplayRepository, mockXhbCourtRepository,
                mockXhbRotationSetsRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);
        // Checks
        EasyMock.verify(mockXhbDisplayRepository);
        assertNotNull(result, NULL);
    }

    @Test
    void testGetDisplayConfigurationNull() {
        Assertions.assertThrows(DisplayNotFoundException.class, () -> {
            classUnderTest.getDisplayConfiguration(0, mockEntityManager);
        });
    }

    @Test
    void testGetDisplayConfigurationMultiSite() {
        // Setup
        List<XhbCourtRoomDao> roomList = new ArrayList<>();
        roomList.add(DummyCourtUtil.getXhbCourtRoomDao());

        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();

        List<XhbCourtSiteDao> xhbCourtSites = new ArrayList<>();
        xhbCourtSites.add(DummyCourtUtil.getXhbCourtSiteDao());
        xhbCourtSites.add(DummyCourtUtil.getXhbCourtSiteDao());

        EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbDisplayDao));
        EasyMock.expect(mockXhbCourtRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(80, "TestCourt")));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtId(EasyMock.isA(Integer.class))).andReturn(xhbCourtSites);
        EasyMock.expect(mockXhbCourtRoomRepository.findByDisplayId(EasyMock.isA(Integer.class))).andReturn(roomList);
        EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbCourtSites.get(0)));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockXhbRotationSetsRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbRotationSetsDao()));

        EasyMock.replay(mockXhbDisplayRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);
        EasyMock.replay(mockXhbCourtRoomRepository);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbRotationSetsRepository);

        // Run
        DisplayConfiguration result =
            classUnderTest.getDisplayConfiguration(0, mockXhbDisplayRepository, mockXhbCourtRepository,
                mockXhbRotationSetsRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

        // Checks
        EasyMock.verify(mockXhbDisplayRepository);
        EasyMock.verify(mockXhbCourtRepository);
        assertNotNull(result, NULL);
    }

    @Test
    void testUpdateDisplayConfiguration() {
        // Setup
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        XhbCourtRoomDao[] roomArray = {DummyCourtUtil.getXhbCourtRoomDao()};
        List<XhbCourtRoomDao> roomList = new ArrayList<>();
        roomList.add(roomArray[0]);
        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();

        DisplayConfiguration displayConfiguration =
            new DisplayConfiguration(xhbDisplayDao, xhbRotationSetsDao, roomArray);
        displayConfiguration.setRotationSetDao(xhbRotationSetsDao);
        displayConfiguration.setCourtRoomDaosWithCourtRoomChanged(roomArray);

        EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbDisplayDao));
        // Going down the isCourtRoomsChanged() route
        EasyMock.expect(mockXhbDisplayRepository.update(xhbDisplayDao)).andReturn(Optional.of(xhbDisplayDao));
        EasyMock.expect(mockXhbCourtRoomRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        // Going down the isRotationSetChanged() route
        EasyMock.expect(mockXhbRotationSetsRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(Optional.of(xhbRotationSetsDao));
        EasyMock.expect(mockXhbDisplayLocationRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        EasyMock.expect(mockXhbCourtRoomRepository.findByDisplayId(EasyMock.isA(Integer.class))).andReturn(roomList);

        EasyMock.replay(mockXhbDisplayRepository);
        EasyMock.replay(mockXhbRotationSetsRepository);
        EasyMock.replay(mockXhbCourtRoomRepository);
        EasyMock.replay(mockXhbDisplayLocationRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);

        final boolean result = true;

        // Run
        classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
            mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository);

        // Checks
        EasyMock.verify(mockXhbDisplayRepository);
        EasyMock.verify(mockXhbRotationSetsRepository);
        EasyMock.verify(mockXhbCourtRoomRepository);
        EasyMock.verify(mockXhbDisplayLocationRepository);
        EasyMock.verify(mockXhbCourtSiteRepository);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testUpdateDisplayConfigurationRotationSetRoute() {
        // Setup
        XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
        XhbCourtRoomDao[] roomArray = {DummyCourtUtil.getXhbCourtRoomDao()};
        XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();

        DisplayConfiguration displayConfiguration =
            new DisplayConfiguration(xhbDisplayDao, xhbRotationSetsDao, roomArray);
        displayConfiguration.setRotationSetDao(xhbRotationSetsDao);

        EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbDisplayDao));
        // Going down the isRotationSetChanged() route
        EasyMock.expect(mockXhbRotationSetsRepository.findById(EasyMock.isA(Long.class)))
            .andReturn(Optional.of(xhbRotationSetsDao));
        EasyMock.expect(mockXhbDisplayLocationRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));

        EasyMock.replay(mockXhbDisplayRepository);
        EasyMock.replay(mockXhbRotationSetsRepository);
        EasyMock.replay(mockXhbDisplayLocationRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);

        final boolean result = true;

        // Run
        classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
            mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository);

        // Checks
        EasyMock.verify(mockXhbDisplayRepository);
        EasyMock.verify(mockXhbRotationSetsRepository);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testUpdateDisplayConfigurationEmptyDisplay() {
        Assertions.assertThrows(DisplayNotFoundException.class, () -> {
            // Setup
            XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
            XhbCourtRoomDao[] roomArray = {DummyCourtUtil.getXhbCourtRoomDao(), DummyCourtUtil.getXhbCourtRoomDao()};
            XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();

            DisplayConfiguration displayConfiguration =
                new DisplayConfiguration(xhbDisplayDao, xhbRotationSetsDao, roomArray);

            EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class))).andReturn(Optional.empty());

            EasyMock.replay(mockXhbDisplayRepository);

            // Run
            classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
                mockEntityManager);
        });
    }

    @Test
    void testUpdateDisplayConfigurationEmptyRotationSet() {
        Assertions.assertThrows(RotationSetNotFoundCheckedException.class, () -> {
            // Setup
            XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
            XhbCourtRoomDao[] roomArray = {DummyCourtUtil.getXhbCourtRoomDao()};
            XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();

            DisplayConfiguration displayConfiguration =
                new DisplayConfiguration(xhbDisplayDao, xhbRotationSetsDao, roomArray);
            displayConfiguration.setRotationSetDao(xhbRotationSetsDao);

            EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(xhbDisplayDao));

            // Going down the isRotationSetChanged() route
            EasyMock.expect(mockXhbRotationSetsRepository.findById(EasyMock.isA(Long.class)))
                .andReturn(Optional.empty());
            EasyMock.expect(mockXhbDisplayLocationRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
            EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));

            EasyMock.replay(mockXhbDisplayRepository);
            EasyMock.replay(mockXhbRotationSetsRepository);
            EasyMock.replay(mockXhbDisplayLocationRepository);
            EasyMock.replay(mockXhbCourtSiteRepository);

            // Run
            classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
                mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository);
        });
    }

    @Test
    void testUpdateDisplayConfigurationEmptyCourtRoom() {
        Assertions.assertThrows(CourtRoomNotFoundException.class, () -> {
            // Setup
            XhbRotationSetsDao xhbRotationSetsDao = DummyPublicDisplayUtil.getXhbRotationSetsDao();
            XhbCourtRoomDao[] roomArray = {DummyCourtUtil.getXhbCourtRoomDao()};
            XhbDisplayDao xhbDisplayDao = DummyPublicDisplayUtil.getXhbDisplayDao();

            DisplayConfiguration displayConfiguration =
                new DisplayConfiguration(xhbDisplayDao, xhbRotationSetsDao, roomArray);
            displayConfiguration.setCourtRoomDaosWithCourtRoomChanged(roomArray);

            EasyMock.expect(mockXhbDisplayRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(xhbDisplayDao));
            // Going down the isCourtRoomsChanged() route
            EasyMock.expect(mockXhbDisplayRepository.update(xhbDisplayDao)).andReturn(Optional.of(xhbDisplayDao));
            EasyMock.expect(mockXhbCourtRoomRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.empty());
            EasyMock.expect(mockXhbDisplayLocationRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
            EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
            EasyMock.expect(mockXhbCourtRoomRepository.findByDisplayId(EasyMock.isA(Integer.class)))
                .andReturn(new ArrayList<>());
            // Going down the isRotationSetChanged() route
            EasyMock.replay(mockXhbDisplayRepository);
            EasyMock.replay(mockXhbCourtRoomRepository);
            EasyMock.replay(mockXhbDisplayLocationRepository);
            EasyMock.replay(mockXhbCourtSiteRepository);

            // Run
            classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
                mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository);
        });
    }

    @Test
    void testGetCourtIdFromDisplayNoDisplayLocation() {
        Integer result = classUnderTest.getCourtIdFromDisplay(Optional.empty(), null, null);
        assertNull(result, NULL);

        EasyMock.expect(mockXhbDisplayLocationRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.empty());
        EasyMock.replay(mockXhbDisplayLocationRepository);
        result = classUnderTest.getCourtIdFromDisplay(Optional.of(DummyPublicDisplayUtil.getXhbDisplayDao()),
            mockXhbDisplayLocationRepository, null);
        assertNull(result, NULL);
    }

    @Test
    void testGetCourtIdFromDisplayNoCourtSite() {
        Optional<XhbDisplayDao> xhbDisplayDao = Optional.of(DummyPublicDisplayUtil.getXhbDisplayDao());
        EasyMock.expect(mockXhbDisplayLocationRepository.findById(xhbDisplayDao.get().getDisplayId()))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class))).andReturn(Optional.empty());
        EasyMock.replay(mockXhbDisplayLocationRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);
        Integer result = classUnderTest.getCourtIdFromDisplay(xhbDisplayDao, mockXhbDisplayLocationRepository,
            mockXhbCourtSiteRepository);
        assertNull(result, NULL);
    }
}
