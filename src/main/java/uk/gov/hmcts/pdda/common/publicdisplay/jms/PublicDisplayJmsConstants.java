package uk.gov.hmcts.pdda.common.publicdisplay.jms;

/**
 * <p>
 * Title: PublicDisplayJMSConstants.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */

public final class PublicDisplayJmsConstants {

    /** Name of the court id message header. */
    public static final String COURT_ID_PROPERTY_NAME = "courtId";

    /** Default connection factory. */
    public static final String DEFAULT_TCF = "PublicDisplayConnectionFactory";

    /** Default destination. */
    public static final String DEFAULT_DESTINATION = "PublicDisplay";
    
    private PublicDisplayJmsConstants() {
    }

}
