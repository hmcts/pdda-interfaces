package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"PMD"})
class AbstractPdConfigReposControllerBeanTest {

    private AbstractPdConfigReposControllerBean beanUnderTest;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);

        beanUnderTest = new AbstractPdConfigReposControllerBean(entityManager,
            mock(XhbClobRepository.class), mock(XhbCourtRepository.class),
            mock(XhbConfigPropRepository.class), mock(XhbCppFormattingRepository.class),
            mock(XhbRotationSetsRepository.class), mock(XhbRotationSetDdRepository.class),
            mock(XhbDisplayTypeRepository.class), mock(XhbDisplayRepository.class),
            mock(XhbDisplayLocationRepository.class), mock(XhbCourtSiteRepository.class),
            mock(XhbCourtRoomRepository.class), mock(PublicDisplayNotifier.class),
            mock(VipDisplayDocumentQuery.class), mock(VipDisplayCourtRoomQuery.class)) {};
    }

    @Test
    void testClearRepositoriesReinitializesRepositories() {
        beanUnderTest.clearRepositories();

        assertNotNull(beanUnderTest.getXhbRotationSetsRepository(),
            "RotationSetsRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbRotationSetDdRepository(),
            "RotationSetDdRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbDisplayTypeRepository(),
            "DisplayTypeRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbDisplayRepository(),
            "DisplayRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbCourtSiteRepository(),
            "CourtSiteRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbCourtRoomRepository(),
            "CourtRoomRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbDisplayLocationRepository(),
            "DisplayLocationRepository should be reinitialized");
        assertNotNull(beanUnderTest.getXhbDisplayDocumentRepository(),
            "DisplayDocumentRepository should be reinitialized");
    }


    @Test
    void testLazyInitializationOfRepositoriesAndHelpers() {
        assertNotNull(beanUnderTest.getPublicDisplayNotifier());
        assertNotNull(beanUnderTest.getXhbRotationSetsRepository());
        assertNotNull(beanUnderTest.getXhbRotationSetDdRepository());
        assertNotNull(beanUnderTest.getXhbDisplayDocumentRepository());
        assertNotNull(beanUnderTest.getXhbDisplayTypeRepository());
        assertNotNull(beanUnderTest.getXhbDisplayRepository());
        assertNotNull(beanUnderTest.getXhbCourtSiteRepository());
        assertNotNull(beanUnderTest.getXhbCourtRoomRepository());
        assertNotNull(beanUnderTest.getXhbDisplayLocationRepository());
        assertNotNull(beanUnderTest.getDisplayRotationSetDataHelper());
        assertNotNull(beanUnderTest.getVipDisplayDocumentQuery());
        assertNotNull(beanUnderTest.getVipDisplayCourtRoomQuery());
        assertNotNull(beanUnderTest.getVipCourtRoomsQuery(true));
    }

}
