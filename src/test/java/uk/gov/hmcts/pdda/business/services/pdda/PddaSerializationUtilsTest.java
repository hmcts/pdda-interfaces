package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.DummyEventUtil;
import uk.gov.hmcts.DummyFileUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**

 * Title: PddaSerializationUtilsTest.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class PddaSerializationUtilsTest {

    @Test
    void testDecodeDeserialize() {
        byte[] decodedEvent =
            PddaSerializationUtils.decodePublicEvent(DummyFileUtil.SERIALIZED_HEARINGSTATUSEVENT);
        assertNotNull(decodedEvent, "decodePublicEvent produced null");
        PublicDisplayEvent event = PddaSerializationUtils.deserializePublicEvent(decodedEvent);
        assertNotNull(event, "deserializePublicEvent produced null");
    }

    @Test
    void testSerializeEncode() {
        PublicDisplayEvent event = DummyEventUtil.getHearingStatusEvent();
        byte[] serialized = PddaSerializationUtils.serializePublicEvent(event);
        assertNotNull(serialized, "serializePublicEvent produced null");
        String encoded = PddaSerializationUtils.encodePublicEvent(serialized);
        assertNotNull(encoded, "encodePublicEvent produced null");
    }
}
