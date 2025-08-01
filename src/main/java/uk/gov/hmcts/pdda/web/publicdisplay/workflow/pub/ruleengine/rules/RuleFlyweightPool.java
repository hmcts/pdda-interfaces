package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions.RulesConfigurationException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

 * Title: RuleFlyweightPool.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Rakesh Lakhani
 * @version 1.0
 */

public final class RuleFlyweightPool {

    /**
     * Pool to store rules that have been loaded from the XML.
     */
    private static final RuleFlyweightPool POOL = new RuleFlyweightPool();

    private static final String RULE_ID = "id";

    private static final String RULE_CLASS = "class";

    private static final String EMPTY_STRING = "";

    private static final Logger LOG = LoggerFactory.getLogger(RuleFlyweightPool.class);

    /**
     * Internal map to store rules by rule id.
     */
    private final Map<String, Rule> ruleMap = new ConcurrentHashMap<>();

    /**
     * Returns a singleton instance of the pool.

     * @return RuleFlyweightPool
     */
    public static RuleFlyweightPool getInstance() {
        return POOL;
    }

    private RuleFlyweightPool() {
        super();
    }

    /**
     * For the attributes passed in remove an instance of the rule and store in the hashmap. This
     * method will not check if the key is already used or if the class has previosly been created.
     * It will just replace it in the map.

     * @param attributes Attributes
     * @throws RulesConfigurationException Exception
     */
    public void loadRule(Attributes attributes) {
        // Get the rule id from the XML and check it has a value
        String ruleId = attributes.getValue(RULE_ID);
        // Get the class name and check it has a value
        String ruleClassName = attributes.getValue(RULE_CLASS);

        checkRuleAttributes(ruleId, ruleClassName);

        Rule rule;
        try {
            // Instantiate a rule
            rule = (Rule) Class.forName(ruleClassName).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            LOG.error("Rule not found for id: {}", ruleId, ex);
            throw new RulesConfigurationException("Rule not found for id:" + ruleId, ex);
        }
        // add to map
        ruleMap.put(ruleId, rule);
    }

    private void checkRuleAttributes(String ruleId, String ruleClassName) {
        if (ruleId == null || EMPTY_STRING.equals(ruleId.trim())) {
            throw new RulesConfigurationException(
                "Invalid document structure. No rule Id specified");
        }
        if (ruleClassName == null || EMPTY_STRING.equals(ruleClassName.trim())) {
            throw new RulesConfigurationException(
                "Invalid document structure. No rule ClassName Id specified");
        }
    }

    /**
     * Get the rule for the rule Id passed in.

     * @param ruleId String
     * @return Rule
     * @throws RulesConfigurationException if the rule has not been declared.
     */
    public Rule getRule(String ruleId) {
        if (ruleId != null && !EMPTY_STRING.equals(ruleId.trim())) {
            Rule rule = ruleMap.get(ruleId);
            if (rule != null) {
                return rule;
            }
        }
        throw new RulesConfigurationException(
            "Attempt to get a rule that has not been declared, rule id=" + ruleId);
    }
}
