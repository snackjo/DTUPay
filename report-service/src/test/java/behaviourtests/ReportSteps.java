package behaviourtests;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;
import report.service.CorrelationId;
import report.service.Payment;
import report.service.ReportService;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ReportSteps {

    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final ReportService reportService = new ReportService(queueMock);
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private String merchantDtuPayId;
    private String customerToken;
    private int amount;
    private String customerDtuPayId;
    private Thread requestThread;
    private Event publishedEvent;


    @Given("that no payments have completed yet")
    public void thatNoPaymentsHaveCompletedYet() {
        assertTrue(reportService.getManagerReport().isEmpty());
    }

    @When("a PaymentCompleted event is received")
    public void aPaymentCompletedEventIsReceived() {
        reportService.handlePaymentCompletedEvent(new Event(ReportService.PAYMENT_COMPLETED,
                new Object[]{"", "merchantDtuPayId", "customerToken", 100, "customerDtuPayId"}));
    }

    @Then("that payment is stored")
    public void thatPaymentIsStored() {
        assertEquals(1, reportService.getManagerReport().size());
    }

    @Given("a completed payment")
    public void aCompletedPayment() {
        merchantDtuPayId = "merchantDtuPayId";
        customerToken = "customerToken";
        amount = 100;
        customerDtuPayId = "customerDtuPayId";
        reportService.handlePaymentCompletedEvent(new Event(ReportService.PAYMENT_COMPLETED
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
        requestThread = new Thread(() -> reportService.handleManagerReportRequested(new Event(ReportService.MANAGER_REPORT_REQUESTED,
                new Object[]{correlationId})));
        requestThread.start();
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock, timeout(10000)).publish(eventCaptor.capture());
        publishedEvent = eventCaptor.getValue();

        assertEquals(eventName, publishedEvent.getType());
    }

    @And("the payment is included")
    public void thePaymentIsIncluded() throws InterruptedException {
        requestThread.join();
        List<Payment> report = (List<Payment>) publishedEvent.getArgument(1, List.class);
        assertEquals(1, report.size());
        Payment payment = new Gson().fromJson(new Gson().toJson(report.get(0)), Payment.class);
        assertEquals(merchantDtuPayId, payment.getMerchantDtuPayId());
        assertEquals(customerToken, payment.getCustomerToken());
        assertEquals(amount, payment.getAmount());
        assertEquals(customerDtuPayId, payment.getCustomerDtuPayId());
    }
}
