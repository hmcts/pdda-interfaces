package uk.gov.hmcts.framework.services.jms;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.JmsServices;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * <p>
 * Title: JMSServicesImpl.
 * </p>
 * <p>
 * Description: Implementation of the <code>JMSServices</code> interface. Provides a range of
 * utilites for sending messages
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Will Fardell
 * @version $Id: JMSServicesImpl.java,v 1.2 2006/07/14 10:26:06 bzjrnl Exp $
 */
public class JmsServicesImpl implements JmsServices {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JmsServicesImpl.class);

    /**
     * Singleton instance.
     */
    private static JmsServicesImpl instance;

    /**
     * Get the singleton instance.
     */
    public static JmsServicesImpl getInstance() {
        synchronized (JmsServicesImpl.class) {
            if (instance == null) {
                instance = new JmsServicesImpl();
            }
            return instance;
        }
    }

    /**
     * Send the message as part of the current XA User Transaction.
     * 
     * @param factories the message factory to use to create and populate the message.
     */
    @Override
    public void send(MessageFactory... factories) {
        try {
            // Use the default XA JMS connection factory
            ConnectionFactory connectionFactory =
                getConnectionFactory("org.apache.activemq.ActiveMQXAConnectionFactory");
            try (Connection connection = connectionFactory.createConnection()) {
                LOG.debug("send({})", connection);
                connection.start();
                send(connection, factories);
            }

        } catch (JMSException jmse) {
            LOG.debug("send({})", jmse.getMessage());
            throw new JmsServicesException(jmse);
        }
    }

    /**
     * Send the messages created by the factories to the current connection.
     * 
     * @param connection Connection
     * @param factories MessageFactory
     * @throws JMSException Exception
     */
    static void send(Connection connection, MessageFactory... factories) throws JMSException {
        // Note that we are taking part in the XA User Transaction so
        // this should not be marked as transacted.
        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            LOG.debug("send({},{})", "Session: ", session);
            send(session, factories);
        }
    }

    /**
     * Send the messages created by the factories using the session.
     * 
     * @param session Session
     * @param factories MessageFactory
     * @throws JMSException Exception
     */
    static void send(Session session, MessageFactory... factories) throws JMSException {
        for (MessageFactory factory : factories) {
            LOG.debug("send({},{})", "MessageFactory: ", factory);
            send(session, factory);
        }
    }

    /**
     * Send the message created by the factory using the session.
     * 
     * @param session Session
     * @param factory MessageFactory
     * @throws JMSException Exception
     */
    static void send(Session session, MessageFactory factory) throws JMSException {
        Message message = factory.create(session);
        Destination destination = getDestination(factory.getDestination());
        try (MessageProducer producer = session.createProducer(destination)) {
            LOG.debug("send({},{}) ", "Message: ", message);
            producer.send(message);
        }
    }

    private static Destination getDestination(String name) {
        return (Destination) lookup(name);
    }

    private static ConnectionFactory getConnectionFactory(String name) {
        return (ConnectionFactory) lookup(name);
    }

    private static Object lookup(String name) {
        try {
            Context context = new InitialContext();
            try {
                return context.lookup(name);
            } finally {
                try {
                    context.close();
                } catch (NamingException ne) {
                    LOG.error("An error occured closing the naming context.", ne);
                }
            }
        } catch (NamingException ne) {
            throw new JmsServicesException(ne);
        }
    }

}
