package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.io.OutputStream;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: FormattingServicesProcessing Test.
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
class FormattingServicesProcessingTest {

    private static final String NULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private Reader mockReader;

    @Mock
    private OutputStream mockOutputStream;

    @Mock
    private BlobHelper mockBlobHelper;

    @InjectMocks
    private final FormattingServices classUnderTest =
        new FormattingServices(mockEntityManager, mockBlobHelper);

    @Test
    void testGetFormattingValue() {
        XhbFormattingDao xhbFormattingDao = DummyFormattingUtil.getXhbFormattingDao();
        FormattingValue result =
            classUnderTest.getFormattingValue(xhbFormattingDao, mockReader, mockOutputStream);
        assertNotNull(result, NULL);
    }
}
