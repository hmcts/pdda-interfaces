package uk.gov.hmcts.framework.util;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: DateTimeUtilities Test.
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
@ExtendWith(EasyMockExtension.class)
class DateTimeUtilitiesTest {

    private static final String NULL = "Result is Null";
   
    @Test
    void testProcessOracleDateParameter() throws ParseException {
        assertNotNull(DateTimeUtilities.processOracleDateParameter("2024-01-01T00:00:00"), NULL);
    }
    
    @Test
    void testProcessOracleDateParameterInvalidFormat() throws ParseException {
        Assertions.assertThrows(ParseException.class, () -> {
            DateTimeUtilities.processOracleDateParameter("Invalid");
        });
    }
    
    @Test
    void testProcessOracleDateParameterForDate() throws ParseException {
        assertNotNull(DateTimeUtilities.processOracleDateParameterForDate("2024-01-01T00:00:00"), NULL);
    }
}
