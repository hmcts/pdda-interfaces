package uk.gov.hmcts.pdda.web.publicdisplay.events;

import java.util.Optional;

@FunctionalInterface
public interface LiveEventProvider {
    Optional<CrLiveEventXmlParser.ParseResult> loadForCourtRoom(Integer courtRoomId);
}