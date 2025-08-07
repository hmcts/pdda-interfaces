package uk.gov.hmcts.pdda.business.services.cppformatting;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;


/**
 * <p>
 * Title: CppFormattingHelper Test.
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
class CppFormattingHelperTest {

    private static final String NULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private final CppFormattingHelper classUnderTest = new CppFormattingHelper();

    @Test
    void testGetLatestPublicDisplayDocument() {
        List<XhbCppFormattingDao> xhbCppFormattingDaos = new ArrayList<>();
        xhbCppFormattingDaos.add(DummyFormattingUtil.getXhbCppFormattingDao());

        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(xhbCppFormattingDaos);

        try (MockedStatic<EntityManagerUtil> staticMock = mockStatic(EntityManagerUtil.class)) {
            staticMock.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCppFormattingDao result =
                classUnderTest.getLatestPublicDisplayDocument(0, mockEntityManager);

            assertNotNull(result, NULL);
        }
    }

}
