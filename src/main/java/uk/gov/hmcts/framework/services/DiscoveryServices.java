package uk.gov.hmcts.framework.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;


/**

 * Title: DiscoveryServices.


 * Description: Used to find concrete impelementations of services


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: DiscoveryServices.java,v 1.3 2006/06/05 12:30:18 bzjrnl Exp $
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class DiscoveryServices {
    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryServices.class);

    private static final DiscoveryServices INSTANCE = new DiscoveryServices();

    /**
     * Get the singleton instance of the service.

     * @return the DiscoveryServices
     */
    public static DiscoveryServices getInstance() {
        return INSTANCE;
    }

    /**
     * Discover the concrete instance of the class, returns null if cant be found. This allows a
     * default to be created in a type safe manor.

     * @param clazz the cla
     * @return a new instance of the clazz or null if cant be found
     */
    public Object createInstance(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz: null");
        }

        String className = getClassName(clazz);
        if (className != null) {
            try {
                Class<?> concreteClazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                if (clazz.isAssignableFrom(concreteClazz)) {
                    Object object = concreteClazz.newInstance();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Discovered concrete implementation " + className + " of "
                            + clazz.getName() + ".");
                    }
                    return object;
                } else {
                    LOG.warn("Concrete implemenation " + className + ", not an instance of "
                        + clazz.getName() + ".");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOG.warn("Could not create " + className + ".", e);
            }
        } else {
            LOG.warn("Could not discover concrete implementation of " + clazz.getName() + ".");
        }

        return null;
    }

    private static String getClassName(Class<?> clazz) {
        String className = getClassNameProperty(clazz);
        if (className != null) {
            return className;
        }
        className = getServiceClassNameIdentifier(clazz);
        if (className != null) {
            return className;
        }
        return null;
    }

    private static String getClassNameProperty(Class<?> clazz) {
        String property = System.getProperty(clazz.getName());
        if (property != null) {
            String trimed = property.trim();
            if (trimed.length() > 0) {
                return trimed;
            }
        }
        return null;
    }

    private static String getServiceClassNameIdentifier(Class<?> clazz) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/services/" + clazz.getName());
        if (url != null) {
            try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(url.openStream()))) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        String trimed = line.trim();
                        if (trimed.length() > 0) {
                            return trimed;
                        }
                    }
                } finally {
                    closeSilently(reader);
                }
            } catch (IOException ioe) {
                LOG.warn("An error occured reading resource " + url + ".", ioe);
            }
        }
        return null;
    }

    private static void closeSilently(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            LOG.warn("An error occured closing the character stream.", ioe);
        }
    }

}
