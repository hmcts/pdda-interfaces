package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;

/**
 * CppStagingInboundHelperTest.
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CppStagingInboundHelperTest {

    private static final String NULL = "Result is Null";
    private static final String TEST = "TEST";

    @Mock
    private static EntityManager mockEntityManager;

    @Mock
    private XhbCppStagingInboundRepository xhbCppStagingInboundRepository;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private final CppStagingInboundHelper classUnderTest =
        new CppStagingInboundHelper(mockEntityManager);

    @AfterEach
    public void resetMocks() {
        Mockito.reset(mockQuery);
    }
    
    @Test
    void testFindNextDocumentByStatus() {
        // Setup
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaos = new ArrayList<>();
        xhbCppStagingInboundDaos.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(xhbCppStagingInboundDaos);

        Mockito.when(xhbCppStagingInboundRepository.findByIdSafe(isA(Integer.class)))
            .thenReturn(Optional.of(DummyPdNotifierUtil.getXhbCppStagingInboundDao()));

        // Run
        List<XhbCppStagingInboundDao> result = classUnderTest.findNextDocumentByStatus(TEST, TEST);

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testFindNextDocumentByStatusNullDocs() {
        // Setup
        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(null);

        // Run
        List<XhbCppStagingInboundDao> result = classUnderTest.findNextDocumentByStatus(TEST, TEST);

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testFindUnrespondedCppMessages() {
        // Setup
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaos = new ArrayList<>();
        xhbCppStagingInboundDaos.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(xhbCppStagingInboundDaos);

        // Run
        List<XhbCppStagingInboundDao> result = classUnderTest.findUnrespondedCppMessages();

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testUpdateCppStagingInbound() {
        // Setup
        XhbCppStagingInboundDao xhbCppStagingInboundDao =
            DummyPdNotifierUtil.getXhbCppStagingInboundDao();

        Mockito.when(xhbCppStagingInboundRepository.update(isA(XhbCppStagingInboundDao.class)))
            .thenReturn(Optional.of(xhbCppStagingInboundDao));

        // Run
        Optional<XhbCppStagingInboundDao> result =
            classUnderTest.updateCppStagingInbound(xhbCppStagingInboundDao, "TestUsername");

        // Checks
        assertNotNull(result, NULL);
    }
}
