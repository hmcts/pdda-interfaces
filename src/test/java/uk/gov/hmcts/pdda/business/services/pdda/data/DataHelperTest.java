package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TooManyMethods")
class DataHelperTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private RepositoryHelper mockRepositoryHelper;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @InjectMocks
    private final DataHelper classUnderTest = new DataHelper(mockRepositoryHelper);

    @Test
    void testDefaultConstructor() {
        DataHelper localClassUnderTest = new DataHelper() {
            @Override
            public RepositoryHelper getRepositoryHelper() {
                return super.getRepositoryHelper();
            }
        };
        localClassUnderTest.getRepositoryHelper();
    }

    @Test
    void testCreateCourtSite() {
        Mockito.when(mockRepositoryHelper.getXhbCourtSiteRepository())
            .thenReturn(mockXhbCourtSiteRepository);

        XhbCourtSiteDao dao = DummyCourtUtil.getXhbCourtSiteDao();
        Optional<XhbCourtSiteDao> result = classUnderTest.createCourtSite(dao.getCourtId(),
            dao.getCourtSiteName(), dao.getCourtSiteCode(), dao.getAddressId());
        assertNotNull(result, NOTNULL);
    }



}
