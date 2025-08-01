package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DefinitivePublicNoticeStatusValue;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublicNoticeXmlHelperTest {


    @InjectMocks
    private final PublicNoticeXmlHelper classUnderTest = PublicNoticeXmlHelper.getInstance();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetManipulatorMap() {
        Map<Integer, List<DefinitivePublicNoticeStatusValue>> result = classUnderTest.getManipulatorMap();
        assertNotNull(result, "Result is Null");
    }
}
