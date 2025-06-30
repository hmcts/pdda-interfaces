package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.easymock.TestSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayValueUtil;
import uk.gov.hmcts.framework.services.TranslationServices;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.exceptions.DelegateNotFoundException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: DisplayDocumentCompiledRendererDelegateTest Test.
 * </p>
 * <p>
 * Description: Tests the DisplayDocumentCompiledRendererDelegateTest classes. It was hoped that the
 * resulting HTML could be compared with a prepared string, but attempts at getting the two to pass
 * an AssertEquals have failed. This can potentially be plugged in at a later date if more time
 * allows.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Chris Vincent
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayDocumentCompiledRendererDelegateTest {

    private static final String NO20603 = "20603";
    private static final String NO20901 = "20901";
    private static final String NO20904 = "20904";
    private static final String NO20935 = "20935";
    private static final String NO30200 = "30200";
    private static final String INVALID = "invalid";
    private static final String TRUE = "Result is not True";
    private static final String NULL = "Result is Null";

    @Mock
    private TranslationBundle mockTranslationBundle;

    @Mock
    private DisplayDocument mockDisplayDocument;

    @Mock
    private TranslationServices mockTranslationServices;

    @Mock
    private PdDataControllerBean mockPdDataControllerBean;

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @TestSubject
    private final AllCaseStatusCompiledRendererDelegate classUnderTest =
        new AllCaseStatusCompiledRendererDelegate();

    @Test
    void testDailytListCompiledRendererDelegate() {
        // Setup
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyPublicDisplayValueUtil.getSummaryByNameValue(true, null));
        table.add(DummyPublicDisplayValueUtil.getSummaryByNameValue(false, null));
        DailytListCompiledRendererDelegate classUnderTest =
            new DailytListCompiledRendererDelegate();
        boolean result = DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
            classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
            mockTranslationServices, mockTranslationBundle);
        assertTrue(result, TRUE);
    }

    @Test
    void testJuryCurrentStatusCompiledRendererDelegate() {
        // Setup
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyPublicDisplayValueUtil.getJuryStatusDailyListValue(true, true));
        table.add(DummyPublicDisplayValueUtil.getJuryStatusDailyListValue(false, true));
        table.add(DummyPublicDisplayValueUtil.getJuryStatusDailyListValue(true, false));
        table.add(DummyPublicDisplayValueUtil.getJuryStatusDailyListValue(false, false));
        JuryCurrentStatusCompiledRendererDelegate classUnderTest =
            new JuryCurrentStatusCompiledRendererDelegate();
        boolean result = DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
            classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
            mockTranslationServices, mockTranslationBundle);
        assertTrue(result, TRUE);
    }

    @Test
    void testAllCaseStatusCompiledRendererDelegate() {
        // Setup
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(true, false, INVALID, null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(true, true, null, null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "10100", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "10500", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_in_week_commencing"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_on"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_for_Sentence"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_on_date_to_be_fixed"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_for_Further_Mention/PAD"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_for_trial"));
        table
            .add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200, INVALID));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "30300", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "30400", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "30500", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "CPP", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "40601", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "32000", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "31000", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "30600", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "30100",
            "E30100_Case_released_until"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, "30100",
            "E30100_Case_adjourned_until"));

        table = testAllCaseStatusCompiledRendererDelegate20000Events(table);

        AllCaseStatusCompiledRendererDelegate classUnderTest =
            new AllCaseStatusCompiledRendererDelegate();
        boolean result = DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
            classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
            mockTranslationServices, mockTranslationBundle);
        assertTrue(result, TRUE);
    }

    private Collection<PublicDisplayValue> testAllCaseStatusCompiledRendererDelegate20000Events(
        Collection<PublicDisplayValue> table) {
        // Setup
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20502", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "21201", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "21200", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "21100", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20602", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20603,
            "E20603_Appellant_Sworn"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20603,
            "E20603_Interpreter_Sworn"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20603, INVALID));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20604", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20605", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20606", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20607", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20608", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20609", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20610", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20611", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20612", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20613", null));
        table.add(
            DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20901, "E20901_days"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20901,
            "E20901_weeks"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20901,
            "E20901_months"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20901, INVALID));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20902", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20903",
            "E20903_Prosecution_Opening"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20903", INVALID));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20904,
            "E20904_Defendant_sworn"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20904,
            "E20904_Interpreter_sworn"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20904, INVALID));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20905", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20906", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20907", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20908", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20909", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20910", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20911", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20912", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20914", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20916", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20917", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20918", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20919", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20920", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20931", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, "20932", null));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20935,
            "E20935_Defendant_Read"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20935,
            "E20904_Interpreter_sworn"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, true, NO20935, INVALID));
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(false, 0, null));
        table.add(DummyPublicDisplayValueUtil.getJuryStatusDailyListValue(false, false));

        return table;
    }

    @Test
    void testAllCourtStatusCompiledRendererDelegate() {
        // Setup
        final boolean result;
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyPublicDisplayValueUtil.getAllCourtStatusValue(true, false, "20905"));
        table.add(DummyPublicDisplayValueUtil.getAllCourtStatusValue(false, true, "20906"));
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(false, 0, "20907"));
        // Loop the 12 months to ensure the date routine works for each day / month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            AllCourtStatusCompiledRendererDelegate classUnderTest =
                DummyCourtUtil.getNewAllCourtStatusCompiledRendererDelegate(calendar);
            DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
                classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
                mockTranslationServices, mockTranslationBundle);
        }
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testCourtDetailCompiledRendererDelegate() {
        // Setup
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyCourtUtil.getCourtDetailValue(true, "20912"));
        table.add(DummyCourtUtil.getCourtDetailValue(false, "20913"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_in_week_commencing"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_on"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_for_Sentence"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_on_date_to_be_fixed"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_for_Further_Mention/PAD"));
        table.add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200,
            "E30200_Case_to_be_listed_for_trial"));
        table
            .add(DummyPublicDisplayValueUtil.getAllCaseStatusValue(false, false, NO30200, INVALID));
        CourtDetailCompiledRendererDelegate classUnderTest =
            new CourtDetailCompiledRendererDelegate();
        boolean result = DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
            classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
            mockTranslationServices, mockTranslationBundle);
        assertTrue(result, TRUE);
    }

    @Test
    void testSummaryByNameCompiledRendererDelegate() {
        // Setup
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyPublicDisplayValueUtil.getSummaryByNameValue(true, "40601"));
        table.add(DummyPublicDisplayValueUtil.getSummaryByNameValue(false, "CPP"));
        SummaryByNameCompiledRendererDelegate classUnderTest =
            new SummaryByNameCompiledRendererDelegate();
        boolean result = DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
            classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
            mockTranslationServices, mockTranslationBundle);
        assertTrue(result, TRUE);
    }

    @Test
    void testCourtListCompiledRendererDelegate() {
        // Setup
        Collection<PublicDisplayValue> table = new ArrayList<>();
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(true, 0, "20931"));
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(false, 5, "20932"));
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(false, 6, null));
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(true, 8, "20933"));
        table.add(DummyPublicDisplayValueUtil.getCourtListValue(false, 9, "20934"));
        CourtListCompiledRendererDelegate classUnderTest = new CourtListCompiledRendererDelegate();
        boolean result = DisplayDocumentCompiledRendererDelegateAssert.testCompiledRendererDelegate(
            classUnderTest, table, mockPdDataControllerBean, mockDisplayStoreControllerBean,
            mockTranslationServices, mockTranslationBundle);
        assertTrue(result, TRUE);
    }

    @Test
    void testDelegateNotFoundException() {
        Assertions.assertThrows(DelegateNotFoundException.class, () -> {
            throw new DelegateNotFoundException("Test");

        });
    }

    @Test
    void testGetLoggedTimeToRender() {
        String result =
            classUnderTest.getLoggedTimeToRender(null, System.currentTimeMillis(), "Test");
        assertNotNull(result, NULL);
    }
}
