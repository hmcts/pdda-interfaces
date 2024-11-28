package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataHelperTest {

    private static final String NOTNULL = "Result is Null";

    @InjectMocks
    private final DataHelper classUnderTest = new DataHelper(Mockito.mock(RepositoryHelper.class));

    @Test
    void testDefaultConstructor() {
        DataHelper result = new DataHelper();
        assertNotNull(result, NOTNULL);
    }

}
