package uk.gov.hmcts.pdda.business.services.pdda;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

import java.util.Base64;

/**
 * <p>
 * Title: PddaSerializationUtils.
 * </p>
 * <p>
 * Description: Holds the methods used to encode, decode, serialize and deserialize events
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 * @version 1.0
 */
public final class PddaSerializationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PddaSerializationUtils.class);
    private static final String EMPTY_STRING = "";

    private PddaSerializationUtils() {
        // Private constructor
    }

    public static byte[] serializePublicEvent(PublicDisplayEvent event) {
        LOG.debug("Serializing event: {}", event);
        return SerializationUtils.serialize(event);
    }

    public static PublicDisplayEvent deserializePublicEvent(byte[] eventBytes) {
        if (eventBytes != null && !EMPTY_STRING.equals(eventBytes)) {
            LOG.debug("Deserializing event: {}", eventBytes);
            return (PublicDisplayEvent) SerializationUtils.deserialize(eventBytes);
        }
        return null;
    }

    public static String encodePublicEvent(byte[] event) {
        LOG.debug("Encoding event: {}", event);
        return new String(Base64.getEncoder().encode(event));
    }

    public static byte[] decodePublicEvent(String encodedEvent) {
        LOG.debug("Decoding event: {}", encodedEvent);
        return Base64.getDecoder().decode(encodedEvent);
    }
}
