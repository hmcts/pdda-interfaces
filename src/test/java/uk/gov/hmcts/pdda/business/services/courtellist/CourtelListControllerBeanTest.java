package uk.gov.hmcts.pdda.business.services.courtellist;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.pdda.CourtelHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: CourtelListControllerBean Test.
 * </p>
 * <p>
 * Description: Unit tests for the CourtelListControllerBean class
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Nathan Toft
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourtelListControllerBeanTest {

    private static final String TRUE = "Result is not True";
    private static final String NOT_NULL = "Result is Not Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private XhbCourtelListRepository mockXhbCourtelListRepository;

    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;

    @Mock
    private final CourtelHelper mockCourtelHelper = new CourtelHelper(mockXhbClobRepository,
        mockXhbCourtelListRepository, mockXhbXmlDocumentRepository);

    @InjectMocks
    private final CourtelListControllerBean classUnderTest =
        new CourtelListControllerBean(mockEntityManager, mockCourtelHelper);

    @Test
    void testDoTask() {
        // Run method
        boolean result;
        try {
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertTrue(result, TRUE);
    }

    @Test
    void testDefaultConstructorEntityManager() {
        CourtelListControllerBean testConstructor =
            new CourtelListControllerBean(mockEntityManager, mockCourtelHelper);
        assertNotNull(testConstructor, NOT_NULL);
    }

    @Test
    void testDefaultConstructor() {
        CourtelListControllerBean testConstructor =
            new CourtelListControllerBean(mockCourtelHelper);
        assertNotNull(testConstructor, NOT_NULL);
    }
}
