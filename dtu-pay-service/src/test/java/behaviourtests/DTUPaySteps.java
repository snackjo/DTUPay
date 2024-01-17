package behaviourtests;

import dtupay.service.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DTUPaySteps {
    Customer customer;
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final DTUPayService service = new DTUPayService(queueMock);
    private Customer customerRegistrationResult;
    private Merchant merchantRegistrationResult;
    private String paymentCompletedResponse;
    private CorrelationId correlationId;
    private DTUPayException tokenRequestException;
    private Merchant merchant;
    private Event publishedEvent;
    private List<Token> tokensGenerated;
    private Thread requestThread;
    private Report reportGenerated;

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
        requestThread = new Thread(() -> customerRegistrationResult = service.registerCustomer(customer));
        requestThread.start();
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock, timeout(10000)).publish(eventCaptor.capture());
        publishedEvent = eventCaptor.getValue();

        assertEquals(eventName, publishedEvent.getType());
        correlationId = publishedEvent.getArgument(0, CorrelationId.class);
    }
    
    @When("a CustomerRegistered event is received")
    public void aCustomerRegisteredEventIsReceived() {
        Customer customer = new Customer();
        customer.setDtuPayId("123");
        service.handleCustomerRegistered(new Event(DTUPayService.CUSTOMER_REGISTERED,
                new Object[] {correlationId, customer}));
    }

    @Then("the customer is registered and his DTUPay id is set")
    public void theCustomerIsRegisteredAndHisDTUPayIdIsSet() throws InterruptedException {
        requestThread.join();
        assertNotNull(customerRegistrationResult.getDtuPayId());
    }

    @When("a payment of {int} is being requested")
    public void aPaymentOfIsBeingRequested(int paymentAmount) {
        String merchantDtuPayId = "12345";
        Token token = new Token();
        token.setId("abcd");

        requestThread = new Thread(() -> paymentCompletedResponse = service.requestPayment(merchantDtuPayId, token, paymentAmount));
        requestThread.start();
    }

    @When("a TokensRequestRejected event is received")
    public void aTokensRequestRejectedEventIsReceived() {
        service.handleTokensRequestRejected(new Event(DTUPayService.TOKENS_REQUEST_REJECTED,
                new Object[] {correlationId}));
    }

    @Then("a DTUPay exception is thrown")
    public void aDTUPayExceptionIsThrown() throws InterruptedException {
        requestThread.join();
        assertNotNull(tokenRequestException);
    }

    @When("a customer requests {int} tokens")
    public void theCustomerRequestsTokens(int tokenAmount) {
        requestThread = new Thread(() -> {
            try{
                tokensGenerated = service.requestTokens("DTUPayId", tokenAmount);
            }catch(DTUPayException e){
                tokenRequestException = e;
            }
        });
        requestThread.start();
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
        requestThread = new Thread(() -> merchantRegistrationResult = service.registerMerchant(merchant));
        requestThread.start();
    }

    @When("a MerchantRegistered event is received")
    public void aMerchantRegisteredEventIsReceived() {
        Merchant merchant = new Merchant();
        merchant.setDtuPayId("123");
        service.handleMerchantRegistered(new Event(DTUPayService.MERCHANT_REGISTERED,
                new Object[] {correlationId, merchant}));
    }

    @Then("the merchant is registered and his DTUPay id is set")
    public void theMerchantIsRegisteredAndHisDTUPayIdIsSet() throws InterruptedException {
        requestThread.join();
        assertNotNull(merchantRegistrationResult.getDtuPayId());
    }

    @When("a PaymentCompleted event is received")
    public void aPaymentCompletedEventIsReceived() {
        service.handlePaymentCompleted(new Event(DTUPayService.PAYMENT_COMPLETED,
                new Object[] {correlationId}));
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() throws InterruptedException {
        requestThread.join();
        assertEquals("Success", paymentCompletedResponse);
    }

    @When("a TokensGenerated event is received")
    public void aTokensGeneratedEventIsReceived() {
        List<Token> tokens = new ArrayList<>();
        int requestedAmount = publishedEvent.getArgument(2, Integer.class);
        for(int i = 0; i < requestedAmount; i++){
            Token token = new Token();
            token.setId(String.valueOf(i));
            tokens.add(token);
        }
        service.handleTokensGenerated(new Event(DTUPayService.TOKENS_GENERATED,
                new Object[]{correlationId, tokens}));
    }

    @Then("{int} tokens are returned")
    public void tokensAreReturned(int tokenAmount) throws InterruptedException {
        requestThread.join();
        assertEquals(tokenAmount, tokensGenerated.size());
    }

    @When("a manager requests a report")
    public void aManagerRequestsAReport() {
        requestThread = new Thread(() -> {
                reportGenerated = service.requestManagerReport();
        });
        requestThread.start();
    }

    @When("a ManagerReportGenerated event is received")
    public void aManagerReportGeneratedEventIsReceived() {
        List<Payment> report = new ArrayList<>();
        report.add(new Payment());
        service.handleManagerReportGenerated(new Event(DTUPayService.MANAGER_REPORT_GENERATED, new Object[]{correlationId, report}));
    }

    @Then("report is returned")
    public void reportIsReturned() throws InterruptedException {
        requestThread.join();
        assertEquals(1, reportGenerated.getPayments().size());
    }

    @When("a merchant requests a report")
    public void aMerchantRequestsAReport() {
        String merchantDtuPayId = "12345";
        requestThread = new Thread(() -> {
            reportGenerated = service.requestMerchantReport(merchantDtuPayId);
        });
        requestThread.start();
    }

    @When("a MerchantReportGenerated event is received")
    public void aMerchantReportGeneratedEventIsReceived() {
        List<Payment> report = new ArrayList<>();
        report.add(new Payment());
        service.handleMerchantReportGenerated(new Event(DTUPayService.MERCHANT_REPORT_GENERATED, new Object[]{correlationId, report}));
    }

    @When("a customer requests a report")
    public void aCustomerRequestsAReport() {
        String customerDtuPayId = "12345";
        requestThread = new Thread(() -> {
            reportGenerated = service.requestCustomerReport(customerDtuPayId);
        });
        requestThread.start();
    }

    @When("a CustomerReportGenerated event is received")
    public void aCustomerReportGeneratedEventIsReceived() {
        List<Payment> report = new ArrayList<>();
        report.add(new Payment());
        service.handleCustomerReportGenerated(new Event(DTUPayService.CUSTOMER_REPORT_GENERATED, new Object[]{correlationId, report}));

    }
}
