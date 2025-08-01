package uk.gov.hmcts.pdda.business.services.pdda;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: BlobHelperTest Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlobHelperTest {

    private static final String NOTNULL = "Result is null";
    private static final String TRUE = "Result is not True";

    @Mock
    private XhbBlobRepository mockXhbBlobRepository;

    @InjectMocks
    private BlobHelper classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new BlobHelper(mockXhbBlobRepository);
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testCreateBlob() {
        // Setup
        byte[] dummyByteArray = {'1', '2', '3'};
        Optional<XhbBlobDao> dummyBlobDao =
            Optional.of(DummyFormattingUtil.getXhbBlobDao(dummyByteArray));
        Mockito.when(mockXhbBlobRepository.update(Mockito.isA(XhbBlobDao.class)))
            .thenReturn(dummyBlobDao);
        // Run
        Long result = classUnderTest.createBlob(dummyByteArray);
        // Checks
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetBlob() {
        // Setup
        Long dummyId = Long.valueOf(1);
        byte[] dummyByteArray = {'1', '2', '3'};
        Optional<XhbBlobDao> dummyBlobDao =
            Optional.of(DummyFormattingUtil.getXhbBlobDao(dummyByteArray));
        Mockito.when(mockXhbBlobRepository.findByIdSafe(Mockito.isA(Long.class)))
            .thenReturn(dummyBlobDao);
        // Run
        byte[] result = classUnderTest.getBlobData(dummyId);
        // Checks
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testUpdateBlob() {
        // Setup
        Long dummyId = Long.valueOf(1);
        byte[] dummyByteArray = {'1', '2', '3'};
        Optional<XhbBlobDao> dummyBlobDao =
            Optional.of(DummyFormattingUtil.getXhbBlobDao(dummyByteArray));
        Mockito.when(mockXhbBlobRepository.findByIdSafe(Mockito.isA(Long.class)))
            .thenReturn(dummyBlobDao);
        Mockito.when(mockXhbBlobRepository.update(Mockito.isA(XhbBlobDao.class)))
            .thenReturn(dummyBlobDao);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateBlob(dummyId, dummyByteArray);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        // Checks
        assertTrue(result, TRUE);
    }

}
