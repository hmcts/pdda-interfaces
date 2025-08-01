package uk.gov.hmcts.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**

 * Title: Applications.


 * Description: Object to allow all the applications that the CS framework handles to be referenced.
 * The exhibit application would be referenced with a <code>Applications.XHIBIT</code> reference.
 * This references the <code>Application</code> inner class. This is an implementation of the type
 * safe pattern.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond

 *         edited Paul Grove 20/09/2002


 *         edited Frederik Vandendriessche 16/11/2002

 * @version 1.1
 */

public final class Applications {

    private static Collection<Application> applicationList = new ArrayList<>();

    public static final Application XHIBIT;

    public static final Application ENABLING_APPS;

    /**
     * Creates all the application objects for the CS Hub and adds them to an ArrayList.
     */
    static {
        XHIBIT = new Application("xhibit");
        applicationList.add(XHIBIT);
        ENABLING_APPS = new Application("ea");
        applicationList.add(ENABLING_APPS);
    }

    private Applications() {
        // empty
    }

    /**
     * Provides a list of Applications running on the CS HUB which the Framework supports.

     * @return Iterator object loaded with application objects which represents all the applications
     *         running on the CS Hub under the Framework
     */
    public static Iterator<Application> getApplications() {
        return applicationList.iterator();
    }

    public static class Application {
        private final String name;

        /**
         * Creates an application.

         * @param name - String the name of the application e.g. Xhibit

         *        Fred VDD: changed access control to public so client.xhibitapplication.Xhibit may
         *        instantiate.
         */
        public Application(String name) {
            this.name = name;
        }

        /**
         * Used to provide a name for the application. This is used for example as a properties
         * object key to create a name space for all applications which access information from the
         * ConfigServices interface.

         * @returns the name of the application
         */
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
