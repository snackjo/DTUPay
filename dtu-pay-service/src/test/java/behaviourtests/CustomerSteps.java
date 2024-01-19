package behaviourtests;

import dtupay.service.DtuPayException;
import dtupay.service.EventNames;
import dtupay.service.Token;
import dtupay.service.customer.Customer;
import dtupay.service.customer.CustomerFacade;
import dtupay.service.customer.CustomerReport;
import dtupay.service.customer.CustomerReportEntry;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

// @author Bastian
public class CustomerSteps {

    private final PublishedEventHolder publishedEventHolder;
    Customer customer;
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final CustomerFacade customerFacade = new CustomerFacade(queueMock);
    private Customer customerRegistrationResult;
    private Thread requestThread;
    private List<Token> tokensGenerated;
    private String errorMessage;
    private DtuPayException tokenRequestException;
    private CustomerReport customerReportGenerated;
    private Exception customerDeregistrationException;

    public CustomerSteps(PublishedEventHolder publishedEventHolder) {
        this.publishedEventHolder = publishedEventHolder;
        this.publishedEventHolder.setQueue(queueMock);
    }

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
        requestThread = new Thread(() -> customerRegistrationResult = customerFacade.registerCustomer(customer));
        requestThread.start();
    }

    @When("a CustomerRegistered event is received")
    public void aCustomerRegisteredEventIsReceived() {
        Customer customer = new Customer();
        customer.setDtuPayId("123");
        customerFacade.handleCustomerRegistered(new Event(EventNames.CUSTOMER_REGISTERED,
                new Object[]{publishedEventHolder.getCorrelationId(), customer}));
    }

    @Then("the customer is registered and his DTUPay id is set")
    public void theCustomerIsRegisteredAndHisDTUPayIdIsSet() throws InterruptedException {
        requestThread.join();
        assertNotNull(customerRegistrationResult.getDtuPayId());
    }

    @When("a customer requests {int} tokens")
    public void theCustomerRequestsTokens(int tokenAmount) {
        requestThread = new Thread(() -> {
            try {
                tokensGenerated = customerFacade.requestTokens("DTUPayId", tokenAmount);
            } catch (DtuPayException e) {
                tokenRequestException = e;
            }
        });
        requestThread.start();
    }

    @When("a TokensGenerated event is received")
    public void aTokensGeneratedEventIsReceived() {
        List<Token> tokens = new ArrayList<>();
        int requestedAmount = publishedEventHolder.getPublishedEvent().getArgument(2, Integer.class);
        for (int i = 0; i < requestedAmount; i++) {
            Token token = new Token();
            token.setId(String.valueOf(i));
            tokens.add(token);
        }
        customerFacade.handleTokensGenerated(new Event(EventNames.TOKENS_GENERATED,
                new Object[]{publishedEventHolder.getCorrelationId(), tokens}));
    }

    @Then("{int} tokens are returned")
    public void tokensAreReturned(int tokenAmount) throws InterruptedException {
        requestThread.join();
        assertEquals(tokenAmount, tokensGenerated.size());
    }

    @When("a TokensRequestRejected event is received")
    public void aTokensRequestRejectedEventIsReceived() {
        errorMessage = "Token request was rejected";
        customerFacade.handleTokensRequestRejected(new Event(EventNames.TOKENS_REQUEST_REJECTED,
                new Object[]{publishedEventHolder.getCorrelationId(), errorMessage}));
    }

    @Then("a DTUPay exception is thrown")
    public void aDTUPayExceptionIsThrown() throws InterruptedException {
        requestThread.join();
        assertNotNull(tokenRequestException);
    }

    @And("the error message comes from the event")
    public void theErrorMessageComesFromTheEvent() throws InterruptedException {
        requestThread.join();
        assertEquals(errorMessage, tokenRequestException.getMessage());
    }

    @When("a customer requests a report")
    public void aCustomerRequestsAReport() {
        String customerDtuPayId = "12345";
        requestThread = new Thread(() -> {
            customerReportGenerated = customerFacade.requestCustomerReport(customerDtuPayId);
        });
        requestThread.start();
    }

    @When("a CustomerReportGenerated event is received")
    public void aCustomerReportGeneratedEventIsReceived() {
        CustomerReport report = new CustomerReport();
        List<CustomerReportEntry> payments = new ArrayList<>();
        payments.add(new CustomerReportEntry());
        report.setPayments(payments);
        customerFacade.handleCustomerReportGenerated(new Event(EventNames.CUSTOMER_REPORT_GENERATED,
                new Object[]{publishedEventHolder.getCorrelationId(), report}));
    }

    @Then("a customer report is returned")
    public void aCustomerReportIsReturned() throws InterruptedException {
        requestThread.join();
        assertEquals(1, customerReportGenerated.getPayments().size());
    }

    @When("a customer requests to be deregistered")
    public void aCustomerRequestsToBeDeregistered() {
        customer = new Customer();
        customer.setDtuPayId("12345");
        requestThread = new Thread(() -> {
            try {
                customerFacade.requestCustomerDeregistration(customer.getDtuPayId());
            } catch (Exception e) {
                customerDeregistrationException = e;
            }

        });
        requestThread.start();
    }

    @When("a CustomerDeregisteredEvent is received")
    public void aCustomerDeregisteredEventIsReceived() {
        Event event = new Event(EventNames.CUSTOMER_DEREGISTERED,
                new Object[]{publishedEventHolder.getCorrelationId(), customer.getDtuPayId()});
        customerFacade.handleCustomerDeregistered(event);
    }

    @Then("the customer deregistration was successful")
    public void theCustomerDeregistrationWasSuccessful() throws InterruptedException {
        requestThread.join();
        assertNull(customerDeregistrationException);
    }
}
