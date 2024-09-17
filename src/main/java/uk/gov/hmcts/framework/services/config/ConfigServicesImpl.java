package uk.gov.hmcts.framework.services.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsConfigurationException;
import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * <p>
 * ConfigServicesImpl.
 * </p>
 * <p>
 * Description: Insulates the application components from knowedge of property storage. Some
 * properties may be stored in XML configuration files and some in name=value properties files or a
 * database.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Pete Raymond
 * @author Paul Grove - completely changed configuration
 * @version 1.1
 * @editors Frederik Vandendriessche - changed BUNDLE_BASENAME accessibility to public so other
 *          components may read from the bundle root.
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public final class ConfigServicesImpl implements ConfigServices {

    private static ConfigServicesImpl instance = new ConfigServicesImpl();

    private static boolean initialized;

    private static Properties applicationProps = new Properties();

    private static final Logger LOG = LoggerFactory.getLogger(ConfigServicesImpl.class);

    public static final String BUNDLE_BASENAME = "config/bundles/";

    private static final String APPLICATIONS_CONFIG_PATH = "/config/application.properties";

    private ConfigServicesImpl() {
    }

    /**
     * Get a reference to the config.
     */
    public static ConfigServicesImpl getInstance() {
        synchronized (ConfigServicesImpl.class) {
            if (!initialized) {
                initialize();
            }
            return instance;
        }
    }

    /**
     * Returns a property from the default CS Hub properties file called cshub.properties which is
     * stored at "classpath root"/config.
     * 
     * @param key the key for which you want he property
     * @return String object of the property for the given key
     */
    @Override
    public String getProperty(String key) {
        return System.getProperty(key, applicationProps.getProperty(key));
    }

    /**
     * The method checks the system property, then the application bundle and then the default value.
     * 
     * @param key the key for which you want he property
     * @param def Default value
     * @return String object of the property for the given key
     */
    @Override
    public String getProperty(String key, String def) {
        return System.getProperty(key, applicationProps.getProperty(key, def));
    }

    /**
     * The method checks the system property, then the specified property file and then the default
     * value.
     * 
     * @param props the property file to search in
     * @param key the key for which you want he property
     * @param def The default value
     * @return String object of the property for the given key
     */
    @Override
    public String getProperty(Properties props, String key, String def) {
        if (props == null) {
            return System.getProperty(key, def);
        } else {
            return System.getProperty(key, props.getProperty(key, def));
        }
    }

    /**
     * Provides a name/value properties file as a properties object to minimise required changed to
     * to other parts of the application while ensuring that configuration data is loaded from a
     * central location.
     * 
     * @param componentName the String for the component type e.g. listimport
     * @return Properties object based on the file loaded
     * @throws CSconfigurationException if configuration files cannot be correctly read
     */
    @Override
    public Properties getProperties(String componentName) {
        String errMsg;
        String file = "/config/components/" + componentName + ".properties";
        LOG.debug("getProperties({})", file);
        try (
            InputStream is = ConfigServices.class.getResourceAsStream(file)) {
            if (is == null) {
                errMsg =
                    "null InputStream for file=" + file + " for componentName=" + componentName;
                CsConfigurationException exception = new CsConfigurationException(errMsg);
                LOG.warn("file " + file + " not found (component will use defaults)");
                throw exception;
            }
            Properties componentProps = new Properties();
            componentProps.load(is);
            return componentProps;
        } catch (IOException e) {
            errMsg = "Error loading file=" + file + " for componentName=" + componentName;
            CsConfigurationException ex = new CsConfigurationException(errMsg, e);
            CsServices.getDefaultErrorHandler().handleError(ex, ConfigServicesImpl.class);
            throw ex;
        }

    }

    private static void initialize() {

        LOG.info(
            ConfigServices.class.getName() + ".initialize(): Thread=" + Thread.currentThread());
        try {
            loadApplicationProperties();
        } catch (IOException e) {
            CsConfigurationException ex = new CsConfigurationException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, ConfigServicesImpl.class);
            throw ex;
        }
        initialized = true;
        LOG.info("Exhibit ConfigServices intialized");
    }

    /**
     * Uses the Applications List Iterator to get the name of each application running on the CS Hub
     * and load each applications master properties file.
     */
    private static void loadApplicationProperties() throws IOException {

        try (
            InputStream is = ConfigServices.class.getResourceAsStream(APPLICATIONS_CONFIG_PATH)) {
            if (is == null) {
                String msg = "file not found: file=" + APPLICATIONS_CONFIG_PATH;
                CsConfigurationException ex = new CsConfigurationException(msg);
                CsServices.getDefaultErrorHandler().handleError(ex, ConfigServices.class);
                throw ex;
            }
            applicationProps.load(is);
            LOG.info("Loading application properties");
        } catch (IOException e) {
            String msg = "Error loading file=" + APPLICATIONS_CONFIG_PATH;
            CsConfigurationException ex = new CsConfigurationException(msg, e);
            CsServices.getDefaultErrorHandler().handleError(ex, ConfigServicesImpl.class);
            throw ex;
        }

    }

    /**
     * Rather than call ResourcrBundle.getResource directly applications call this method which then
     * locates the correct prperties file and then calls ResourcrBundle.getResource with the correct
     * file. This is done to minimise required changes to other parts of the application while
     * ensuring that configuration data is loaded from a central location.
     * 
     * @param baseName see {@link java.util.ResourceBundle ResourceBundle} class description.
     * @return The correct {@link java.util.ResourceBundle ResourceBundle} from the central
     *         repository
     * @throws CsConfigurationException Exception
     */
    @Override
    public ResourceBundle getBundle(String baseName) {

        return ResourceBundle.getBundle(ConfigServicesImpl.BUNDLE_BASENAME + baseName);
    }

    /**
     * Rather than call ResourcrBundle.getResource directly applications call this method which then
     * locates the correct prperties file and then calls ResourcrBundle.getResource with the correct
     * file. This is done to minimise required changes to other parts of the application while
     * ensuring that configuration data is loaded from a central location.
     * 
     * @param baseName see {@link java.util.ResourceBundle ResourceBundle} class description.
     * @param locale see {@link java.util.ResourceBundle ResourceBundle} class description.
     * @return The correct {@link java.util.ResourceBundle ResourceBundle} from the central
     *         repository
     * @throws CsConfigurationException Exception
     */
    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {

        String errMsg;
        try {
            return ResourceBundle.getBundle(ConfigServicesImpl.BUNDLE_BASENAME + baseName, locale);
        } catch (MissingResourceException e) {
            errMsg = "Resource bundle " + ConfigServicesImpl.BUNDLE_BASENAME + baseName 
                + "_" + locale + " not available";
            CsConfigurationException ex = new CsConfigurationException(errMsg, e);
            CsServices.getDefaultErrorHandler().handleError(ex, ConfigServicesImpl.class, errMsg);
            throw ex;
        }
    }

    /**
     * Rather than call ResourcrBundle.getResource directly applications call this method which then
     * locates the correct prperties file and then calls ResourcrBundle.getResource with the correct
     * file. This is done to minimise required changes to other parts of the application while
     * ensuring that configuration data is loaded from a central location.
     * 
     * @param baseName see {@link java.util.ResourceBundle ResourceBundle} class description.
     * @param locale see {@link java.util.ResourceBundle ResourceBundle} class description.
     * @param loader the ClassLoader to load the resource from.
     * @return The correct {@link java.util.ResourceBundle ResourceBundle} from the central
     *         repository
     */
    @Override
    public ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
        return ResourceBundle.getBundle(ConfigServicesImpl.BUNDLE_BASENAME + baseName, locale, loader);
    }

}
