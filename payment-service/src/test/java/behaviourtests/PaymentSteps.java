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

import java.util.ArrayList;
import java.util.List;
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
    private volatile boolean allStarted = false;
    private final List<Thread> threads = new ArrayList<>();


    @When("a PaymentRequested event is received")
    public void aPaymentRequestedEventIsReceived() {
        correlationId1 = CorrelationId.randomId();
        paymentRequestedEvent1 = new Event(PaymentService.PAYMENT_REQUESTED, new Object[]{"merchantDtuPayId", "customerToken", 5, correlationId1});
        paymentService.handlePaymentRequested(paymentRequestedEvent1);
    }

    @And("a CustomerBankAccountFound event is received")
    public void aCustomerBankAccountFoundEventIsReceived() {
        customerBankAccountFoundEvent1 = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1, "customerDtuPayId"});
        paymentService.handleCustomerBankAccountFound(customerBankAccountFoundEvent1);
    }

    @And("a MerchantBankAccountFound event is received")
    public void aMerchantBankAccountFoundEventIsReceived() {
        merchantBankAccountFoundEvent1 = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1});
        paymentService.handleMerchantBankAccountFound(merchantBankAccountFoundEvent1);
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
        customerBankAccountFoundEvent1 = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1, ""});
    }

    @And("a MerchantBankAccountFound event")
    public void aMerchantBankAccountFoundEvent() {
        merchantBankAccountFoundEvent1 = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId1});
    }

    @When("they are all received at the same time")
    public void theyAreAllReceivedAtTheSameTime() {
        threads.add(createPublishEventThread(paymentRequestedEvent1, paymentService::handlePaymentRequested));
        threads.add(createPublishEventThread(customerBankAccountFoundEvent1, paymentService::handleCustomerBankAccountFound));
        threads.add(createPublishEventThread(merchantBankAccountFoundEvent1, paymentService::handleMerchantBankAccountFound));

        startAllThreadsAtTheSameTime();
    }

    private Thread createPublishEventThread(Event event, Consumer<Event> handler) {
        return new Thread(() -> {
            try {
                while (!allStarted) {
                    Thread.onSpinWait();
                }
                handler.accept(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Then("only one {string} event is published")
    public void onlyOneEventIsPublished(String eventName) throws InterruptedException {
        waitForAllThreadsToFinish();
        verify(queueMock, times(1)).publish(eventCaptor.capture());
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
        customerBankAccountFoundEvent2 = new Event(PaymentService.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId2, ""});
    }

    @And("another MerchantBankAccountFound event")
    public void anotherMerchantBankAccountFoundEvent() {
        merchantBankAccountFoundEvent2 = new Event(PaymentService.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{"", correlationId2});
    }

    @Then("two {string} event is published")
    public void twoEventIsPublished(String eventName) throws InterruptedException {
        waitForAllThreadsToFinish();
        verify(queueMock, times(2)).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
    }

    @And("the two events have different correlation id")
    public void theTwoEventsHaveDifferentCorrelationId() throws InterruptedException {
        waitForAllThreadsToFinish();
        verify(queueMock, times(2)).publish(eventCaptor.capture());
        assertNotEquals(eventCaptor.getAllValues().get(0), eventCaptor.getAllValues().get(1));
    }

    @When("events from both payments are received at the same time")
    public void eventsFromBothPaymentsAreReceivedAtTheSameTime() {
        threads.add(createPublishEventThread(paymentRequestedEvent1, paymentService::handlePaymentRequested));
        threads.add(createPublishEventThread(customerBankAccountFoundEvent1, paymentService::handleCustomerBankAccountFound));
        threads.add(createPublishEventThread(merchantBankAccountFoundEvent1, paymentService::handleMerchantBankAccountFound));
        threads.add(createPublishEventThread(paymentRequestedEvent2, paymentService::handlePaymentRequested));
        threads.add(createPublishEventThread(customerBankAccountFoundEvent2, paymentService::handleCustomerBankAccountFound));
        threads.add(createPublishEventThread(merchantBankAccountFoundEvent2, paymentService::handleMerchantBankAccountFound));
        startAllThreadsAtTheSameTime();
    }

    @And("it contains all information used")
    public void itContainsAllInformationUsed() throws InterruptedException {
        waitForAllThreadsToFinish();
        verify(queueMock).publish(eventCaptor.capture());
        Event paymentCompletedEvent = eventCaptor.getValue();

        String merchantDtuPayId = paymentRequestedEvent1.getArgument(0, String.class);
        String customerToken = paymentRequestedEvent1.getArgument(1, String.class);
        int amount = paymentRequestedEvent1.getArgument(2, Integer.class);
        String customerDtuPayId = customerBankAccountFoundEvent1.getArgument(2, String.class);

        assertEquals(correlationId1, paymentCompletedEvent.getArgument(0, CorrelationId.class));
        assertEquals(merchantDtuPayId, paymentCompletedEvent.getArgument(1, String.class));
        assertEquals(customerToken, paymentCompletedEvent.getArgument(2, String.class));
        assertEquals(amount, (long)paymentCompletedEvent.getArgument(3, Integer.class));
        assertEquals(customerDtuPayId, paymentCompletedEvent.getArgument(4, String.class));
    }

    private void startAllThreadsAtTheSameTime() {
        for(Thread thread : threads) {
            thread.start();
        }
        allStarted = true;
    }

    private void waitForAllThreadsToFinish() throws InterruptedException {
        for(Thread thread : threads) {
            thread.join();
        }
    }
}
