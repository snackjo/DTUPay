package behaviourtests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;
import payment.service.CorrelationId;
import payment.service.PaymentService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class PaymentSteps {

    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private final PaymentService paymentService = new PaymentService(queueMock);
    private CorrelationId correlationId;


    @When("a PaymentRequested event is received")
    public void aPaymentRequestedEventIsReceived() {
        correlationId = CorrelationId.randomId();
        Event event = new Event(PaymentService.PAYMENT_REQUESTED, new Object[]{null, null, null, correlationId});
        paymentService.handlePaymentRequested(event);
    }

    @And("a CustomerBankAccountFound event is received")
    public void aCustomerBankAccountFoundEventIsReceived() {
        Event event = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{null, correlationId});
        paymentService.handleCustomerBankAccountFound(event);
    }

    @And("a MerchantBankAccountFound event is received")
    public void aMerchantBankAccountFoundEventIsReceived() {
        Event event = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{null, correlationId});
        paymentService.handleMerchantBankAccountFound(event);
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock).publish(eventCaptor.capture());
        Event publishedEvent = eventCaptor.getValue();
        assertEquals(eventName, publishedEvent.getType());
    }
}
