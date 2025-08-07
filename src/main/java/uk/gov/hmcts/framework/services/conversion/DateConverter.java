package uk.gov.hmcts.framework.services.conversion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * A series of methods to convert between old-style Date/Calendar values and new LocalDate /
 * LocalDateTime values.
 * 
 * @author scottatwell
 *
 */
public class DateConverter {

    protected DateConverter() {
        // Protected constructor
    }

    public static LocalDateTime convertDateToLocalDateTime(Date inDate) {
        return LocalDateTime.ofInstant(inDate.toInstant(), ZoneId.systemDefault());
    }
}
