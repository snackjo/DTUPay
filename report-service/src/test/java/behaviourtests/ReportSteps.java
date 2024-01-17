package behaviourtests;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;
import report.service.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ReportSteps {

    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final ReportService reportService = new ReportService(queueMock);
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private String merchantDtuPayId;
    private Token customerToken;
    private int amount;
    private String customerDtuPayId;
    private Thread requestThread;
    private Event publishedEvent;
    private Payment payment;


    @Given("that no payments have completed yet")
    public void thatNoPaymentsHaveCompletedYet() {
        assertTrue(reportService.getManagerReport().isEmpty());
    }

    @When("a PaymentCompleted event is received")
    public void aPaymentCompletedEventIsReceived() {
        reportService.handlePaymentCompletedEvent(new Event(EventNames.PAYMENT_COMPLETED,
                new Object[]{"", "merchantDtuPayId", new Token("123"), 100, "customerDtuPayId"}));
    }

    @Then("that payment is stored")
    public void thatPaymentIsStored() {
        assertEquals(1, reportService.getManagerReport().size());
    }

    @Given("a completed payment")
    public void aCompletedPayment() {
        merchantDtuPayId = "merchantDtuPayId";
        customerToken = new Token("123");
        amount = 100;
        customerDtuPayId = "customerDtuPayId";

        payment = new Payment();
        payment.setMerchantDtuPayId(merchantDtuPayId);
        payment.setCustomerToken(customerToken);
        payment.setAmount(amount);
        payment.setCustomerDtuPayId(customerDtuPayId);

        reportService.handlePaymentCompletedEvent(new Event(EventNames.PAYMENT_COMPLETED
                , new Object[]{
                "",
                merchantDtuPayId,
                customerToken,
                amount,
                customerDtuPayId
        }));
    }

    @When("a ManagerReportRequested event is received")
    public void aManagerReportRequestedEventIsReceived() {
        CorrelationId correlationId = new CorrelationId(UUID.randomUUID().toString());
        requestThread = new Thread(() -> reportService.handleManagerReportRequested(new Event(EventNames.MANAGER_REPORT_REQUESTED,
                new Object[]{correlationId})));
        requestThread.start();
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock, timeout(10000)).publish(eventCaptor.capture());
        publishedEvent = eventCaptor.getValue();

        assertEquals(eventName, publishedEvent.getType());
    }

    @And("has information relevant for a manager")
    public void hasInformationRelevantForAManager() throws InterruptedException {
        requestThread.join();
        ManagerReport report = publishedEvent.getArgument(1, ManagerReport.class);
        assertEquals(1, report.getPayments().size());
        ManagerReportEntry payment = new Gson().fromJson(new Gson().toJson(report.getPayments().get(0)), ManagerReportEntry.class);
        assertEquals(merchantDtuPayId, payment.getMerchantDtuPayId());
        assertEquals(customerToken, payment.getCustomerToken());
        assertEquals(amount, payment.getAmount());
        assertEquals(customerDtuPayId, payment.getCustomerDtuPayId());
    }

    @When("a CustomerReportRequested event is received")
    public void aCustomerReportRequestedEventIsReceived() {
        CorrelationId correlationId = new CorrelationId(UUID.randomUUID().toString());
        requestThread = new Thread(() -> reportService.handleCustomerReportRequested(new Event(EventNames.CUSTOMER_REPORT_REQUESTED,
                new Object[]{correlationId, customerDtuPayId})));
        requestThread.start();
    }

    @And("has information relevant for a customer")
    public void hasInformationRelevantForACustomer() throws InterruptedException {
        requestThread.join();
        CustomerReport report = publishedEvent.getArgument(1, CustomerReport.class);
        assertEquals(1, report.getPayments().size());
        CustomerReportEntry payment = new Gson().fromJson(new Gson().toJson(report.getPayments().get(0)), CustomerReportEntry.class);
        assertEquals(merchantDtuPayId, payment.getMerchantDtuPayId());
        assertEquals(customerToken, payment.getCustomerToken());
        assertEquals(amount, payment.getAmount());
    }

    @When("a MerchantReportRequested event is received")
    public void aMerchantReportRequestedEventIsReceived() {
        CorrelationId correlationId = new CorrelationId(UUID.randomUUID().toString());
        requestThread = new Thread(() -> reportService.handleMerchantReportRequested(new Event(EventNames.MERCHANT_REPORT_GENERATED,
                new Object[]{correlationId, merchantDtuPayId})));
        requestThread.start();
    }

    @And("has information relevant for a merchant")
    public void hasInformationRelevantForAMerchant() throws InterruptedException {
        requestThread.join();
        MerchantReport report = publishedEvent.getArgument(1, MerchantReport.class);
        assertEquals(1, report.getPayments().size());
        MerchantReportEntry payment = new Gson().fromJson(new Gson().toJson(report.getPayments().get(0)), MerchantReportEntry.class);
        assertEquals(customerToken, payment.getCustomerToken());
        assertEquals(amount, payment.getAmount());
    }
}
