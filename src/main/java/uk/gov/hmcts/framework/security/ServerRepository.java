package uk.gov.hmcts.framework.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**

 * Title: ServerRepository.


 * Description: This class provides utility methods to get information on


 * servers to which a client can connect.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Meeraj Kunnumpurath
 * @version 1.0
 */

public final class ServerRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ServerRepository.class);

    private static final String AUTHENTICATION_URL = ".AUTHENTICATION_URL";
    private static final String INITIAL_CONTEXT_FACTORY = ".INITIAL_CONTEXT_FACTORY";
    private static final String PROVIDER_URL = ".PROVIDER_URL";
    private static final String SERVER_NOT_FOUND = " server not found";
    /**
     * This is the config key that stores the server list.
     */
    public static final String SERVERS_KEY = "servers";

    /**
     * This is the config key for the default server.
     */
    public static final String DEFAULT_SERVER_KEY = "defaultServer";

    /**
     * The name of the default server.
     */
    public static final String DEFAULT_SERVER;

    /**
     * A hashmap of servers.
     */
    private static Map<String, Server> repository;

    /**
     * Static initializer initializes the server repository.
     */
    static {
        LOG.debug("Inside ServerRepository static - start");
        if (System.getProperty(DEFAULT_SERVER_KEY) != null) {
            DEFAULT_SERVER = System.getProperty(DEFAULT_SERVER_KEY);
        } else {
            DEFAULT_SERVER = CsServices.getConfigServices().getProperty(DEFAULT_SERVER_KEY);
        }
        repository = new ConcurrentHashMap<>();

        // Get the list of servers
        ConfigServices config = CsServices.getConfigServices();
        String servers = config.getProperty(SERVERS_KEY);

        StringTokenizer tok = new StringTokenizer(servers);

        // Add the server information in the repository
        while (tok.hasMoreTokens()) {
            String serverName = tok.nextToken();
            repository.put(serverName, getServer(serverName));
        }
        LOG.debug("Inside ServerRepository static - end");
    }

    private static Server getServer(String serverName) {
        return new Server(serverName);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ServerRepository() {
        // private constructor
    }

    /**
     * getServers - This method returns the list of servers.

     * @return StringSet
     */
    public static Set<String> getServers() {
        return repository.keySet();
    }

    /**
     * getAuthenticationURL - This method returns the authentication URL for the named server.

     * @param serverName String
     * @return String
     */
    public static String getAuthenticationUrl(String serverName) {

        Server server = repository.get(serverName);
        if (server == null) {
            throw new IllegalArgumentException(serverName + SERVER_NOT_FOUND);
        }
        return server.getAuthenticationUrl();

    }

    /**
     * getProviderURL - This method returns the provider URL for the named server.

     * @param serverName String
     * @return String
     */
    public static String getProviderUrl(String serverName) {

        Server server = repository.get(serverName);
        if (server == null) {
            throw new IllegalArgumentException(serverName + SERVER_NOT_FOUND);
        }
        return server.getProviderUrl();

    }

    /**
     * getInitialContextFactory - This method returns the initial context factory for the named server.

     * @param serverName String
     * @return String
     */
    public static String getInitialContextFactory(String serverName) {

        LOG.info("In getInitialContextFactory:server={}", serverName);
        Server server = repository.get(serverName);
        if (server == null) {
            throw new IllegalArgumentException(serverName + SERVER_NOT_FOUND);
        }
        LOG.info("In getInitialContextFactory:initialcontextfactory={}",
             server.getInitialContextFactory());
        return server.getInitialContextFactory();

    }

    /**
     * The private inner class represents a server.

     * Title:


     * Description:


     * Copyright: Copyright (c) 2002


     * Company: EDS

     * @author Meeraj Kunnumpurath
     * @version 1.0
     */
    private static class Server {

        /**
         * The authentication URL for the server.
         */
        private final String authenticationUrl;

        /**
         * The provider URL for the server.
         */
        private final String providerUrl;

        /**
         * The initial context factory for the server.
         */
        private final String initialContextFactory;

        /**
         * Accepts the server name and initializes the authentication and provider URLs and initial
         * context factory.

         * @param serverName String
         */
        public Server(String serverName) {

            ConfigServices config = CsServices.getConfigServices();

            // Get the authentication URL
            if (System.getProperty(serverName + AUTHENTICATION_URL) != null) {
                authenticationUrl = System.getProperty(serverName + AUTHENTICATION_URL);
            } else {
                authenticationUrl = config.getProperty(serverName + AUTHENTICATION_URL);
            }
            // Get the provider URL
            if (System.getProperty(serverName + PROVIDER_URL) != null) {
                providerUrl = System.getProperty(serverName + PROVIDER_URL);
            } else {
                providerUrl = config.getProperty(serverName + PROVIDER_URL);
            }
            // Get the initial context factory
            if (System.getProperty(serverName + INITIAL_CONTEXT_FACTORY) != null) {
                initialContextFactory = System.getProperty(serverName + INITIAL_CONTEXT_FACTORY);
            } else {
                initialContextFactory = config.getProperty(serverName + INITIAL_CONTEXT_FACTORY);
            }
        }

        /**
         * Returns the authentication URL for the server.

         * @return String
         */
        public String getAuthenticationUrl() {
            return authenticationUrl;
        }

        /**
         * Returns the provider URL for the server.

         * @return String
         */
        public String getProviderUrl() {
            return providerUrl;
        }

        /**
         * Returns the initial context factory for the server.

         * @return String
         */
        public String getInitialContextFactory() {
            return initialContextFactory;
        }

    }

}
