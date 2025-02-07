package uk.gov.hmcts.pdda.business.services.pdda;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: CourtelHelperReposTest.
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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.LawOfDemeter")
class CourtelHelperReposTest {

    private static final String NULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private XhbCourtelListRepository mockXhbCourtelListRepository;

    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;

    @Mock
    private BlobHelper mockBlobHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;
    
    @Mock
    private ConfigPropMaintainer mockConfigPropMaintainer;
    
    @Mock
    private CathHelper mockCathHelper;
    
    private CourtelHelper classUnderTest;

    @BeforeEach
    public void setup() {
        classUnderTest = new CourtelHelper(mockXhbClobRepository, mockXhbCourtelListRepository,
            mockXhbXmlDocumentRepository, mockBlobHelper, mockXhbConfigPropRepository,
            mockXhbCourtRepository);
    }

    @AfterEach
    public void teardown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testGetConfigPropMaintainer() {
        expectEntityManager();
        ConfigPropMaintainer result =
            classUnderTest.getConfigPropMaintainer();
        assertNotNull(result, NULL);

        ReflectionTestUtils.setField(classUnderTest, "configPropMaintainer",
            mockConfigPropMaintainer);
        result = classUnderTest.getConfigPropMaintainer();
        assertNotNull(result, NULL);
    }
    
    @Test
    void testGetCathHelper() {
        expectEntityManager();
        CathHelper result =
            classUnderTest.getCathHelper();
        assertNotNull(result, NULL);

        ReflectionTestUtils.setField(classUnderTest, "cathHelper",
            mockCathHelper);
        result = classUnderTest.getCathHelper();
        assertNotNull(result, NULL);
    }
    
    @Test
    void testGetXhbCourtelListRepository() {
        expectEntityManager();
        XhbCourtelListRepository result =
            classUnderTest.getXhbCourtelListRepository();
        assertNotNull(result, NULL);

        ReflectionTestUtils.setField(classUnderTest, "xhbCourtelListRepository",
            mockXhbCourtelListRepository);
        result = classUnderTest.getXhbCourtelListRepository();
        assertNotNull(result, NULL);
    }
    
    @Test
    void testGetXhbCourtRepository() {
        expectEntityManager();
        XhbCourtRepository result =
            classUnderTest.getXhbCourtRepository();
        assertNotNull(result, NULL);

        ReflectionTestUtils.setField(classUnderTest, "xhbCourtRepository",
            mockXhbCourtRepository);
        result = classUnderTest.getXhbCourtRepository();
        assertNotNull(result, NULL);
    }

    @Test
    void testNullEntityManager() {
        Mockito.mockStatic(EntityManagerUtil.class);
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        XhbCourtelListRepository result = classUnderTest.getXhbCourtelListRepository();
        assertNotNull(result, NULL);
    }

    private void expectEntityManager() {
        ReflectionTestUtils.setField(classUnderTest, "entityManager", mockEntityManager);
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
    }
}