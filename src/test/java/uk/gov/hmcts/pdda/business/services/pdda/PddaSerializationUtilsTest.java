package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyEventUtil;
import uk.gov.hmcts.DummyFileUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.events.PublicDisplayEvent;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: PddaSerializationUtilsTest.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class PddaSerializationUtilsTest {
    @Test
    void testDeserialize() {
        String decodedEvent =
            PddaSerializationUtils.decodePublicEvent(DummyFileUtil.SERIALIZED_HEARINGSTATUSEVENT);
        assertNotNull(decodedEvent, "decodePublicEvent produced null");
        // PublicDisplayEvent event = PddaSerializationUtils.deserializePublicEvent(decodedEvent);
        // assertNotNull(event, "deserializePublicEvent produced null");
    }

    @Test
    void testSerialize() {
        PublicDisplayEvent event = DummyEventUtil.getHearingStatusEvent();

        String serialized = PddaSerializationUtils.serializePublicEvent(event);
        assertNotNull(serialized, "serializePublicEvent produced null");
        String encoded = PddaSerializationUtils.encodePublicEvent(serialized);
        assertNotNull(encoded, "encodePublicEvent produced null");
    }
}
