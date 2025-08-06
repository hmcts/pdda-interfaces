package uk.gov.hmcts.framework.services;

/**

 * Title: XSL Services.


 * Description: Insulates the application components from knowedge of XSL.


 * Copyright: Copyright (c) 2002


 * Company: EDS

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

     * @return the ResourceServices singleton instance
     */
    public static final XslServices getInstance() {
        return INSTANCE;
    }
}
