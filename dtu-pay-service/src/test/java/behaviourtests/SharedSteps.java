package behaviourtests;

import dtupay.service.CorrelationId;
import io.cucumber.java.en.Then;
import messaging.Event;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

// @author Peter
public class SharedSteps {
    private final PublishedEventHolder publishedEventHolder;

    public SharedSteps(PublishedEventHolder publishedEventHolder) {
        this.publishedEventHolder = publishedEventHolder;
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(publishedEventHolder.getQueue(), timeout(10000)).publish(eventCaptor.capture());
        Event publishedEvent = eventCaptor.getValue();

        assertEquals(eventName, publishedEvent.getType());

        publishedEventHolder.setPublishedEvent(publishedEvent);
        publishedEventHolder.setCorrelationId(publishedEvent.getArgument(0, CorrelationId.class));
    }
}
