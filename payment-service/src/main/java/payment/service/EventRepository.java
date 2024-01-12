package payment.service;

import messaging.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventRepository {
    private final Map<CorrelationId, List<Event>> correlationIdToEvent = new ConcurrentHashMap<>();

    public void putEvent(CorrelationId correlationId, Event event) {
        correlationIdToEvent.computeIfAbsent(correlationId, k -> Collections.synchronizedList(new ArrayList<>())).add(event);
    }

    public List<Event> getEvents(CorrelationId correlationId){
        return correlationIdToEvent.get(correlationId);
    }

}
