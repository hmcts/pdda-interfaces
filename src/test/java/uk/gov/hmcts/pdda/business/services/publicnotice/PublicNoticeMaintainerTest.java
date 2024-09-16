package uk.gov.hmcts.pdda.business.services.publicnotice;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DefinitivePublicNoticeStatusValue;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DisplayablePublicNoticeValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublicNoticeMaintainerTest {

    private static final Integer COURT_ROOM_ID = Integer.valueOf(81);
    private static final String TRUE = "Result is not True";

    @BeforeAll
    public static void setUp() {
        // No setup required
    }

    @AfterAll
    public static void tearDown() {
        // No teardown required
    }

    @Test
    void testUpdateIsActive() {
        // Setup
        boolean result = false;
        DisplayablePublicNoticeValue displayablePublicNoticeValue =
            DummyPdNotifierUtil.getDisplayablePublicNoticeValue();
        // Run
        try {
            PublicNoticeMaintainer.updateIsActive(displayablePublicNoticeValue,
                DummyPdNotifierUtil.getXhbConfiguredPublicNoticeDao("0"));
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateActiveStatus() {
        List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaos = new ArrayList<>();
        xhbConfiguredPublicNoticeDaos.add(DummyPdNotifierUtil.getXhbConfiguredPublicNoticeDao("0"));
        boolean result = testUpdateActiveStatuses(xhbConfiguredPublicNoticeDaos);
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdateActiveStatusEmptyList() {
        Assertions.assertThrows(PublicNoticeException.class, () -> {
            List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaos = new ArrayList<>();
            testUpdateActiveStatuses(xhbConfiguredPublicNoticeDaos);
        });
    }

    private boolean testUpdateActiveStatuses(List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaos) {
        // Setup
        DefinitivePublicNoticeStatusValue definitivePublicNoticeStatusValue =
            DummyPdNotifierUtil.getDefinitivePublicNoticeStatusValue();
        // Run
        PublicNoticeMaintainer.updateActiveStatus(COURT_ROOM_ID, definitivePublicNoticeStatusValue, true,
            xhbConfiguredPublicNoticeDaos);

        return true;
    }
}
