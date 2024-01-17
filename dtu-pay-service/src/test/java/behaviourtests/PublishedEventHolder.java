package behaviourtests;

import dtupay.service.CorrelationId;
import lombok.Data;
import messaging.Event;
import messaging.MessageQueue;

@Data
public class PublishedEventHolder {
    private MessageQueue queue;
    private Event publishedEvent;
    private CorrelationId correlationId;
}
