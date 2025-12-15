package uk.gov.hmcts.pdda.business.services.publicdisplay;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AbstractPdConfigDisplaysControllerBean#getDisplayDocuments().
 */
@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
class AbstractPdConfigDisplaysControllerBeanTest {

    @Mock
    private XhbDisplayDocumentRepository displayDocumentRepository;

    /**
     * Concrete test subclass overriding only the repository accessor used by the method under test.
     * This avoids needing to construct the full superclass state.
     */
    private static class TestDisplaysController extends AbstractPdConfigDisplaysControllerBean {
        private final XhbDisplayDocumentRepository repo;

        TestDisplaysController(XhbDisplayDocumentRepository repo) {
            super(); // uses default no-arg constructor
            this.repo = repo;
        }

        @Override
        protected XhbDisplayDocumentRepository getXhbDisplayDocumentRepository() {
            return repo;
        }
    }

    @Test
    void getDisplayDocuments_returnsArrayOfDocuments_whenRepositoryHasEntries() {
        // arrange
        XhbDisplayDocumentDao doc1 = mock(XhbDisplayDocumentDao.class);
        XhbDisplayDocumentDao doc2 = mock(XhbDisplayDocumentDao.class);
        List<XhbDisplayDocumentDao> docs = Arrays.asList(doc1, doc2);

        when(displayDocumentRepository.findAllSafe()).thenReturn(docs);

        TestDisplaysController controller = new TestDisplaysController(displayDocumentRepository);

        // act
        XhbDisplayDocumentDao[] result = controller.getDisplayDocuments();

        // assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.length, "Expected two documents in the result array");
        // preserve ordering
        assertSame(doc1, result[0], "First element should be the first returned dao");
        assertSame(doc2, result[1], "Second element should be the second returned dao");
        verify(displayDocumentRepository, times(1)).findAllSafe();
        verifyNoMoreInteractions(displayDocumentRepository);
    }

    @Test
    void getDisplayDocuments_returnsEmptyArray_whenRepositoryReturnsEmptyList() {
        // arrange
        when(displayDocumentRepository.findAllSafe()).thenReturn(Collections.emptyList());

        TestDisplaysController controller = new TestDisplaysController(displayDocumentRepository);

        // act
        XhbDisplayDocumentDao[] result = controller.getDisplayDocuments();

        // assert
        assertNotNull(result, "Result should not be null (should be an empty array)");
        assertEquals(0, result.length, "Expected empty array when repository returns empty list");
        verify(displayDocumentRepository, times(1)).findAllSafe();
        verifyNoMoreInteractions(displayDocumentRepository);
    }
}
