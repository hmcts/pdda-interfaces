package uk.gov.hmcts.framework.castor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaRegExpEvaluator implements org.exolab.castor.util.RegExpEvaluator {
    private Pattern pattern;

    @Override
    public boolean matches(String value) {
        Matcher mmatches = pattern.matcher(value);
        return mmatches.matches();
    }

    @Override
    public void setExpression(String rexpr) {
        pattern = Pattern.compile(rexpr);
    }
}
