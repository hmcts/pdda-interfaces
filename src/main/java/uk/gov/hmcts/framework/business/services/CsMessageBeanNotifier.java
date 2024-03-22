package uk.gov.hmcts.framework.business.services;

import jakarta.annotation.Resource;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import jakarta.jms.XASession;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.Serializable;

/**
 * A template class extracted to the framework to ease development of message driven bean notifiers.
 * 
 * <p>Class based upon original version (written by Meeraj) of
 * uk.gov.hmcts.pdda.common.publicdisplay.jms.MessageSender.
 *
 * <p>WARNING
 * 
 * <p>This class should no longer be used, use JMSServices instead. Will Fardell. 11/07/2006.
 * 
 * <p>WARNING
 * 
 * @author tz0d5m
 * @version $Id: CSMessageBeanNotifier.java,v 1.8 2006/07/12 15:52:35 bzjrnl Exp $
 */
public abstract class CsMessageBeanNotifier {
    /** Log4J Logger instance for this class. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ActiveMQConnectionFactory connectionFactory;

    /** Topic connection factory that is used. */
    private TopicConnection topicConnection;

    @Resource(name = "PDDATopic")
    private Topic topic;

    /**
     * Initialises the JNDI properties, topic name and connection factory.
     */
    protected CsMessageBeanNotifier() {

        try {
            // Create the topic connection...
            this.topicConnection = (TopicConnection) getConnectionFactory().createConnection();
            log.debug("Topic connection created");
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
            throw new CsUnrecoverableException(e);
        }
    }

    /**
     * Method to close this notifier instances <code>TopicConnection</code>, no exceptions will be
     * closed, any errors will only be logged.
     * 
     * @see jakarta.jms.TopicConnection#close()
     */
    public final void close() {
        log.debug("close() - Attempting to close the topic connection");

        if (this.topicConnection != null) {
            try {
                this.topicConnection.close();
                log.debug("close() - Topic connection closed");
            } catch (JMSException t) {
                // ignore all errors whilst closing the connection...
                log.error("Error whilst closing the topic connection", t);
            }
        }
    }

    /**
     * Send the message passed in to the required queue.
     * 
     * @param message A Serializable message.
     * @throws IllegalArgumentException if the passed in message is null.
     * @throws CsUnrecoverableException if any other error occurs.
     */
    protected final void sendMessage(Serializable message) {
        if (message == null) {
            log.debug("sendMessage({}) Message is null", message);
            throw new IllegalArgumentException("message cannot be null");
        }

        log.debug("CSMessageBeanNotifier::sendMessage: {}", message);

        try (
            // The message needs to be transacted so that it will not be
            // delivered before the database changes it signals have gone
            // through...
            TopicSession topicSession = this.topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)) {
            enlistXaResource(topicSession);

            // Create the object message...
            ActiveMQObjectMessage msg =
                (ActiveMQObjectMessage) topicSession.createObjectMessage(message);
            msg.setTrustAllPackages(true);

            // Set up the message headers...
            setMessageHeader(msg);

            // Create the publisher...
            try (TopicPublisher tp = topicSession.createPublisher(getTopic())) {
 
                // Publish the message...
                tp.publish(msg);
                log.debug("sendMessage - published.");
            }
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
            throw new CsUnrecoverableException(e);
        }
    }

    /**
     * Set any headers on the message.
     * 
     * @param message The message on which to set headers.
     * @throws JMSException When there is a problem with setting the header.
     */
    protected abstract void setMessageHeader(ObjectMessage message) throws JMSException;

    /**
     * Extracted helper method used to enlist the <code>TopicSession</code> with the transaction if
     * it should be handled as an XA resource (which is deemed so if the transaction is an instance
     * of <code>javax.xml.XASession</code>.
     * 
     * @param topicSession The <code>TopicSession</code> that we want to enlist as an XA resource.
     * @throws CsUnrecoverableException if any error occurs.
     */
    private void enlistXaResource(TopicSession topicSession) {
        // not sure about this particular implementation, however, it is
        // copied from a working version, except that this is made more
        // general (to not use weblogic specific classes)...
        if (topicSession instanceof XASession) {
            log.debug("Attempting to register with the XA transaction...");
        }
    }

    private Topic getTopic() {
        if (topic == null) {
            topic = InitializationService.getInstance().getTopic();
        }
        return topic;
    }

    private ActiveMQConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = InitializationService.getInstance().getActiveMqConnectionFactory();
        }
        return connectionFactory;
    }
}
