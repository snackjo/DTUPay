package behaviourtests;

import dtupay.service.CorrelationId;
import dtupay.service.Customer;
import dtupay.service.DTUPayService;
import dtupay.service.Token;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DTUPaySteps {
    Customer customer;
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final DTUPayService service = new DTUPayService(queueMock);
    private Customer registrationResult;
    private CorrelationId correlationId;

    @Given("a customer with empty DTUPay id")
    public void aCustomerWithEmptyDTUPayId() {
        customer = new Customer();
        customer.setCprNumber("11111111-17");
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        assertNull(customer.getDtuPayId());
    }

    @When("the customer is being registered")
    public void theCustomerIsBeingRegistered() {
        new Thread(() -> registrationResult = service.registerCustomer(customer)).start();
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock, timeout(10000)).publish(eventCaptor.capture());
        Event publishedEvent = eventCaptor.getValue();

        assertEquals(eventName, publishedEvent.getType());

        correlationId = getCorrelationId(publishedEvent);
    }

    @When("a CustomerRegistered event is received")
    public void aCustomerRegisteredEventIsReceived() {
        Customer customer = new Customer();
        customer.setFirstName(this.customer.getFirstName());
        customer.setDtuPayId("123");
        service.handleCustomerRegistered(new Event(DTUPayService.CUSTOMER_REGISTERED,
                new Object[] {customer, correlationId}));
    }

    @Then("the customer is registered and his DTUPay id is set")
    public void theCustomerIsRegisteredAndHisDTUPayIdIsSet() {
        assertNotNull(registrationResult.getDtuPayId());
    }

    @When("a payment of {int} is being requested")
    public void aPaymentOfIsBeingRequested(int paymentAmount) {
        String merchantDtuPayId = "12345";
        Token token = new Token("abcd");

        new Thread(() -> service.requestPayment(merchantDtuPayId, token, paymentAmount)).start();
    }

    private CorrelationId getCorrelationId(Event event) {
        Optional<CorrelationId> firstCorrelationId = Arrays.stream(event.getArguments())
                .filter(CorrelationId.class::isInstance)
                .map(CorrelationId.class::cast)
                .findFirst();

        return firstCorrelationId.orElse(null);
    }
}
