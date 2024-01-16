package behaviourtests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import report.service.ReportService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ReportSteps {

    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final ReportService reportService = new ReportService(queueMock);


    @Given("that no payments have completed yet")
    public void thatNoPaymentsHaveCompletedYet() {
        assertTrue(reportService.getReports().isEmpty());
    }

    @When("a PaymentCompleted event is received")
    public void aPaymentCompletedEventIsReceived() {
        reportService.handlePaymentCompletedEvent(new Event(ReportService.PAYMENT_COMPLETED,
                new Object[]{"", "merchantDtuPayId", "customerToken", 100, "customerDtuPayId"}));
    }

    @Then("that payment is stored")
    public void thatPaymentIsStored() {
        assertEquals(1, reportService.getReports().size());
    }
}
