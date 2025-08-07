package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.Attributes;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions.RulesConfigurationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RuleFlyWeightPoolTest {

    @Mock
    private Attributes mockAttributes;

    @InjectMocks
    private final RuleFlyweightPool classUnderTest = RuleFlyweightPool.getInstance();

    @BeforeAll
    public static void setUp() {
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    void testRuleFlyweightPoolSuccess() {
        // Setup
        Rule rule = new ReportingRestrictionChangedRule();
        String ruleId = rule.getClass().getName();
        String ruleClass = rule.getClass().getName();
        // Run
        testRuleFlyweightPool(ruleId, ruleClass);
        // Checks
        assertNotNull(classUnderTest.getRule(ruleId), "Result is Null");
    }

    @Test
    void testRuleFlyweightPoolNoRuleId() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            // Setup
            Rule rule = new ReportingRestrictionChangedRule();
            String ruleId = null;
            String ruleClass = rule.getClass().getName();
            // Run
            testRuleFlyweightPool(ruleId, ruleClass);
        });
    }

    @Test
    void testRuleFlyweightPoolNoClass() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            // Setup
            Rule rule = new ReportingRestrictionChangedRule();
            String ruleId = rule.getClass().getName();
            String ruleClass = null;
            // Run
            testRuleFlyweightPool(ruleId, ruleClass);
        });
    }

    @Test
    void testRuleFlyweightPoolInvalidClass() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            // Setup
            String ruleId = "Invalid";
            String ruleClass = "Invalid";
            // Run
            testRuleFlyweightPool(ruleId, ruleClass);
        });
    }

    private void testRuleFlyweightPool(String ruleId, String ruleClass) {
        // Expects
        Mockito.when(mockAttributes.getValue("id")).thenReturn(ruleId);
        Mockito.when(mockAttributes.getValue("class")).thenReturn(ruleClass);
        // Run
        classUnderTest.loadRule(mockAttributes);
    }

    @Test
    void testRulesConfigurationExceptionNullValue() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            RuleFlyweightPool classUnderTest = RuleFlyweightPool.getInstance();
            classUnderTest.getRule(null);
        });
    }

    @Test
    void testRulesConfigurationExceptionInvalidValue() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            RuleFlyweightPool classUnderTest = RuleFlyweightPool.getInstance();
            classUnderTest.getRule("Invalid");
        });
    }
}
