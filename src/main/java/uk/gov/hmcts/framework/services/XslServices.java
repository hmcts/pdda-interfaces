package uk.gov.hmcts.framework.services;

/**
 * <p>
 * Title: XSL Services.
 * </p>
 * <p>
 * Description: Insulates the application components from knowedge of XSL.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author William Fardell (Xdevelopment 2003)
 * @version 1.0
 */
public class XslServices extends TransformServices {

    /**
     * The singleton instance.
     */
    private static final XslServices INSTANCE = new XslServices();

    /**
     * Get the singleton.
     * 
     * @return the ResourceServices singleton instance
     */
    public static final XslServices getInstance() {
        return INSTANCE;
    }
}
