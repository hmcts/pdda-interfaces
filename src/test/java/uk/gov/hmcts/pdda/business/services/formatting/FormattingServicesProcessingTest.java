package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
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

 * Title: FormattingServicesProcessing Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FormattingServicesProcessingTest {

    private static final String NULL = "Result is Null";

    @Mock
    private Reader mockReader;

    @Mock
    private OutputStream mockOutputStream;


    @InjectMocks
    private final FormattingServices classUnderTest = new FormattingServices(
        EasyMock.createMock(EntityManager.class), EasyMock.createMock(BlobHelper.class));

    @Test
    void testGetFormattingValue() {
        XhbFormattingDao xhbFormattingDao = DummyFormattingUtil.getXhbFormattingDao();
        FormattingValue result =
            classUnderTest.getFormattingValue(xhbFormattingDao, mockReader, mockOutputStream);
        assertNotNull(result, NULL);
    }
}
