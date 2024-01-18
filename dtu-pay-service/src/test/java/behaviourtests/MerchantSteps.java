package behaviourtests;

import dtupay.service.EventNames;
import dtupay.service.Token;
import dtupay.service.merchant.Merchant;
import dtupay.service.merchant.MerchantFacade;
import dtupay.service.merchant.MerchantReport;
import dtupay.service.merchant.MerchantReportEntry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class MerchantSteps {

    private final PublishedEventHolder publishedEventHolder;
    private Merchant merchant;
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final MerchantFacade merchantFacade = new MerchantFacade(queueMock);
    private Merchant merchantRegistrationResult;
    private String paymentCompletedResponse;
    private Thread requestThread;
    private MerchantReport merchantReportGenerated;
    private Exception merchantDeregistrationException;

    public MerchantSteps(PublishedEventHolder publishedEventHolder) {
        this.publishedEventHolder = publishedEventHolder;
        this.publishedEventHolder.setQueue(queueMock);
    }

    @Given("a merchant with empty DTUPay id")
    public void aMerchantWithEmptyDTUPayId() {
        merchant = new Merchant();
        merchant.setCprNumber("11111111merchant-17");
        merchant.setFirstName("firstName");
        merchant.setLastName("lastName");
        assertNull(merchant.getDtuPayId());
    }

    @When("the merchant is being registered")
    public void theMerchantIsBeingRegistered() {
        requestThread = new Thread(() -> merchantRegistrationResult = merchantFacade.registerMerchant(merchant));
        requestThread.start();
    }

    @When("a MerchantRegistered event is received")
    public void aMerchantRegisteredEventIsReceived() {
        Merchant merchant = new Merchant();
        merchant.setDtuPayId("123");
        merchantFacade.handleMerchantRegistered(new Event(EventNames.MERCHANT_REGISTERED,
                new Object[]{publishedEventHolder.getCorrelationId(), merchant}));
    }

    @Then("the merchant is registered and his DTUPay id is set")
    public void theMerchantIsRegisteredAndHisDTUPayIdIsSet() throws InterruptedException {
        requestThread.join();
        assertNotNull(merchantRegistrationResult.getDtuPayId());
    }

    @When("a payment of {int} is being requested")
    public void aPaymentOfIsBeingRequested(int paymentAmount) {
        String merchantDtuPayId = "12345";
        Token token = new Token();
        token.setId("abcd");

        requestThread = new Thread(() -> paymentCompletedResponse = merchantFacade.requestPayment(merchantDtuPayId, token, paymentAmount));
        requestThread.start();
    }

    @When("a PaymentCompleted event is received")
    public void aPaymentCompletedEventIsReceived() {
        merchantFacade.handlePaymentCompleted(new Event(EventNames.PAYMENT_COMPLETED,
                new Object[]{publishedEventHolder.getCorrelationId()}));
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() throws InterruptedException {
        requestThread.join();
        assertEquals("Success", paymentCompletedResponse);
    }

    @When("a merchant requests a report")
    public void aMerchantRequestsAReport() {
        String merchantDtuPayId = "12345";
        requestThread = new Thread(() -> {
            merchantReportGenerated = merchantFacade.requestMerchantReport(merchantDtuPayId);
        });
        requestThread.start();
    }

    @When("a MerchantReportGenerated event is received")
    public void aMerchantReportGeneratedEventIsReceived() {
        MerchantReport report = new MerchantReport();
        List<MerchantReportEntry> payments = new ArrayList<>();
        payments.add(new MerchantReportEntry());

        report.setPayments(payments);
        merchantFacade.handleMerchantReportGenerated(new Event(EventNames.MERCHANT_REPORT_GENERATED,
                new Object[]{publishedEventHolder.getCorrelationId(), report}));
    }

    @Then("a merchant report is returned")
    public void merchantReportIsReturned() throws InterruptedException {
        requestThread.join();
        assertEquals(1, merchantReportGenerated.getPayments().size());
    }

    @When("a merchant requests to be deregistered")
    public void aMerchantRequestsToBeDeregistered() {
        String merchantDtuPayId = "12345";
        requestThread = new Thread(() -> {
            try {
                merchantFacade.requestMerchantDeregistration(merchantDtuPayId);
            } catch (Exception e) {
                merchantDeregistrationException = e;
            }

        });
        requestThread.start();
    }

    @When("a MerchantDeregisteredEvent is received")
    public void aMerchantDeregisteredEventIsReceived() {
        Event event = new Event(EventNames.MERCHANT_DEREGISTERED,
                new Object[]{publishedEventHolder.getCorrelationId()});
        merchantFacade.handleMerchantDeregistered(event);
    }

    @Then("the merchant deregistration was successful")
    public void theMerchantDeregistrationWasSuccessful() throws InterruptedException {
        requestThread.join();
        assertNull(merchantDeregistrationException);
    }
}
