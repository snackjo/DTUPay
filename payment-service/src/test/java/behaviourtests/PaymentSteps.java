package behaviourtests;

import dtu.ws.fastmoney.BankService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;
import payment.service.CorrelationId;
import payment.service.PaymentService;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;


public class PaymentSteps {

    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final BankService bankMock = mock(BankService.class);
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private final PaymentService paymentService = new PaymentService(queueMock, bankMock);
    private CorrelationId correlationId1;
    private Event paymentRequestedEvent1;
    private Event customerBankAccountFoundEvent1;
    private Event merchantBankAccountFoundEvent1;
    private CorrelationId correlationId2;
    private Event paymentRequestedEvent2;
    private Event customerBankAccountFoundEvent2;
    private Event merchantBankAccountFoundEvent2;


    @When("a PaymentRequested event is received")
    public void aPaymentRequestedEventIsReceived() {
        correlationId1 = CorrelationId.randomId();
        Event event = new Event(PaymentService.PAYMENT_REQUESTED, new Object[]{"", "", 5, correlationId1});
        paymentService.handlePaymentRequested(event);
    }

    @And("a CustomerBankAccountFound event is received")
    public void aCustomerBankAccountFoundEventIsReceived() {
        Event event = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1});
        paymentService.handleCustomerBankAccountFound(event);
    }

    @And("a MerchantBankAccountFound event is received")
    public void aMerchantBankAccountFoundEventIsReceived() {
        Event event = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1});
        paymentService.handleMerchantBankAccountFound(event);
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock).publish(eventCaptor.capture());
        Event publishedEvent = eventCaptor.getValue();
        assertEquals(eventName, publishedEvent.getType());
    }

    @Given("that a merchant wants to start a transaction")
    public void thatAMerchantWantsToStartATransaction() {
        correlationId1 = CorrelationId.randomId();
    }

    @And("a PaymentRequested event")
    public void aPaymentRequestedEvent() {
        paymentRequestedEvent1 = new Event(PaymentService.PAYMENT_REQUESTED, new Object[]{"", "", 10, correlationId1});
    }

    @And("a CustomerBankAccountFound event")
    public void aCustomerBankAccountFoundEvent() {
        customerBankAccountFoundEvent1 = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1});
    }

    @And("a MerchantBankAccountFound event")
    public void aMerchantBankAccountFoundEvent() {
        merchantBankAccountFoundEvent1 = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1});
    }

    @When("they are all received at the same time")
    public void theyAreAllReceivedAtTheSameTime() {
        Thread thread1 = createPublishEventThread(paymentRequestedEvent1, paymentService::handlePaymentRequested);
        Thread thread2 = createPublishEventThread(customerBankAccountFoundEvent1, paymentService::handleCustomerBankAccountFound);
        Thread thread3 = createPublishEventThread(merchantBankAccountFoundEvent1, paymentService::handleMerchantBankAccountFound);
        thread1.start();
        thread2.start();
        thread3.start();
    }

    private Thread createPublishEventThread(Event event, Consumer<Event> handler) {
        return new Thread(() -> {
            try {
                handler.accept(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Then("only one {string} event is published")
    public void onlyOneEventIsPublished(String eventName) {
        verify(queueMock, timeout(5000).times(1)).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
    }

    @Given("that another merchant wants to start a transaction")
    public void thatAnotherMerchantWantsToStartATransaction() {
        correlationId2 = CorrelationId.randomId();
    }

    @And("another PaymentRequested event")
    public void anotherPaymentRequestedEvent() {
        paymentRequestedEvent2 = new Event(PaymentService.PAYMENT_REQUESTED, new Object[]{"", "", 10, correlationId2});
    }

    @And("another CustomerBankAccountFound event")
    public void anotherCustomerBankAccountFoundEvent() {
        customerBankAccountFoundEvent2 = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId2});
    }

    @And("another MerchantBankAccountFound event")
    public void anotherMerchantBankAccountFoundEvent() {
        merchantBankAccountFoundEvent2 = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId2});
    }

    @Then("two {string} event is published")
    public void twoEventIsPublished(String eventName) {
        verify(queueMock, timeout(5000).times(2)).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
    }

    @And("the two events have different correlation id")
    public void theTwoEventsHaveDifferentCorrelationId() {
        verify(queueMock, timeout(5000).times(2)).publish(eventCaptor.capture());
        assertNotEquals(eventCaptor.getAllValues().get(0), eventCaptor.getAllValues().get(1));
    }

    @When("events from both payments are received at the same time")
    public void eventsFromBothPaymentsAreReceivedAtTheSameTime() {
        Thread thread1 = createPublishEventThread(paymentRequestedEvent1, paymentService::handlePaymentRequested);
        Thread thread2 = createPublishEventThread(customerBankAccountFoundEvent1, paymentService::handleCustomerBankAccountFound);
        Thread thread3 = createPublishEventThread(merchantBankAccountFoundEvent1, paymentService::handleMerchantBankAccountFound);
        Thread thread4 = createPublishEventThread(paymentRequestedEvent2, paymentService::handlePaymentRequested);
        Thread thread5 = createPublishEventThread(customerBankAccountFoundEvent2, paymentService::handleCustomerBankAccountFound);
        Thread thread6 = createPublishEventThread(merchantBankAccountFoundEvent2, paymentService::handleMerchantBankAccountFound);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
    }
}
