package uk.gov.hmcts.pdda.business.services.publicdisplay;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

@SuppressWarnings({"static-access", "PMD"})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @InjectMocks
    private final DisplayConfigurationHelper classUnderTest = new DisplayConfigurationHelper();

    @BeforeEach
    void setup() {
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(mockEntityManager);
        EntityManagerUtil.setEntityManagerFactory(mockFactory);
    }

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

        Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(displayDao));
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbRotationSetsDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByCourtId(Mockito.anyInt()))
            .thenReturn(xhbCourtSites);
        Mockito.when(mockXhbCourtRoomRepository.findByDisplayIdSafe(Mockito.anyInt()))
            .thenReturn(new ArrayList<>());
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.empty());

        // Run
        DisplayConfiguration result = classUnderTest.getDisplayConfiguration(0,
            mockXhbDisplayRepository, mockXhbCourtRepository, mockXhbRotationSetsRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

        // Assert
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

        Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(xhbDisplayDao));
        Mockito.when(mockXhbCourtRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(80, "TestCourt")));
        Mockito.when(mockXhbCourtSiteRepository.findByCourtIdSafe(Mockito.anyInt()))
            .thenReturn(xhbCourtSites);
        Mockito.when(mockXhbCourtRoomRepository.findByDisplayIdSafe(Mockito.anyInt()))
            .thenReturn(roomList);
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(xhbCourtSites.get(0)));
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbRotationSetsDao()));

        // Run
        DisplayConfiguration result = classUnderTest.getDisplayConfiguration(0,
            mockXhbDisplayRepository, mockXhbCourtRepository, mockXhbRotationSetsRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

        // Assert
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

        Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(xhbDisplayDao));
        // Going down the isCourtRoomsChanged() route
        Mockito.when(mockXhbDisplayRepository.update(xhbDisplayDao))
            .thenReturn(Optional.of(xhbDisplayDao));
        Mockito.when(mockXhbCourtRoomRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        // Going down the isRotationSetChanged() route
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.anyLong()))
            .thenReturn(Optional.of(xhbRotationSetsDao));
        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        Mockito.when(mockXhbCourtRoomRepository.findByDisplayIdSafe(Mockito.anyInt()))
            .thenReturn(roomList);

        final boolean result = true;

        // Run
        classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
            mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRepository);

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

        Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(xhbDisplayDao));
        // Going down the isRotationSetChanged() route
        Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.anyLong()))
            .thenReturn(Optional.of(xhbRotationSetsDao));
        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));

        final boolean result = true;

        // Run
        classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
            mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
            mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRepository);

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

            Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.empty());

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

            Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.of(xhbDisplayDao));

            // Going down the isRotationSetChanged() route
            Mockito.when(mockXhbRotationSetsRepository.findByIdSafe(Mockito.anyLong()))
                .thenReturn(Optional.empty());
            Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
            Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));

            // Run
            classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
                mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRepository);
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

            Mockito.when(mockXhbDisplayRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.of(xhbDisplayDao));
            // Going down the isCourtRoomsChanged() route
            Mockito.when(mockXhbDisplayRepository.update(xhbDisplayDao))
                .thenReturn(Optional.of(xhbDisplayDao));
            Mockito.when(mockXhbCourtRoomRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.empty());
            Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
            Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
                .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
            Mockito.when(mockXhbCourtRoomRepository.findByDisplayIdSafe(Mockito.anyInt()))
                .thenReturn(new ArrayList<>());
            // Going down the isRotationSetChanged() route

            // Run
            classUnderTest.updateDisplayConfiguration(displayConfiguration, mockPublicDisplayNotifier,
                mockXhbDisplayRepository, mockXhbRotationSetsRepository, mockXhbCourtRoomRepository,
                mockXhbDisplayLocationRepository, mockXhbCourtSiteRepository, mockXhbCourtRepository);
        });
    }

    @Test
    void testGetCourtIdFromDisplayNoDisplayLocation() {
        Integer result = classUnderTest.getCourtIdFromDisplay(Optional.empty(), null, null);
        assertNull(result, NULL);

        Mockito.when(mockXhbDisplayLocationRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.empty());
        result = classUnderTest.getCourtIdFromDisplay(Optional.of(DummyPublicDisplayUtil.getXhbDisplayDao()),
            mockXhbDisplayLocationRepository, null);
        assertNull(result, NULL);
    }

    @Test
    void testGetCourtIdFromDisplayNoCourtSite() {
        Optional<XhbDisplayDao> xhbDisplayDao = Optional.of(DummyPublicDisplayUtil.getXhbDisplayDao());
        Mockito.when(
                mockXhbDisplayLocationRepository.findByIdSafe(xhbDisplayDao.get().getDisplayId()))
            .thenReturn(Optional.of(DummyPublicDisplayUtil.getXhbDisplayLocationDao()));
        Mockito.when(mockXhbCourtSiteRepository.findByIdSafe(Mockito.anyInt()))
            .thenReturn(Optional.empty());
        Integer result = classUnderTest.getCourtIdFromDisplay(xhbDisplayDao, mockXhbDisplayLocationRepository,
            mockXhbCourtSiteRepository);
        assertNull(result, NULL);
    }
}
