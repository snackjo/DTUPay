package behaviourtests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;
import token.service.*;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

// @author Peter
public class TokenSteps {
    private Customer customer;
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final TokenService tokenService = new TokenService(queueMock, customerRepository);
    private List<Token> generatedTokens;
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

    @When("a CustomerRegistered event is received")
    public void aCustomerRegisteredEventIsReceived() {
        customer = new Customer();
        customer.setDtuPayId("cid1");
        CorrelationId correlationId = CorrelationId.randomId();

        Event event = new Event(EventNames.CUSTOMER_REGISTERED, new Object[]{correlationId, customer});
        tokenService.handleCustomerRegistered(event);
    }

    @Then("a customer is created with {int} tokens")
    public void aCustomerIsCreatedWithTokens(int tokenAmount) {
        Customer createdCustomer = customerRepository.getCustomer(customer.getDtuPayId());

        assertNotNull(createdCustomer);
        assertEquals(tokenAmount, createdCustomer.getTokens().size());
    }

    @Given("a registered customer with {int} tokens")
    public void aRegisteredCustomerWithTokens(int tokenAmount) {
        customer = new Customer();
        customer.setDtuPayId("cid1");
        generatedTokens = Token.generateTokens(tokenAmount);
        customer.addTokens(generatedTokens);

        customerRepository.addCustomer(customer);
        assertEquals(tokenAmount, customer.getTokens().size());
    }

    @When("a TokensRequested event for a customer is received for {int} tokens")
    public void aTokensRequestedEventForACustomerIsReceivedForTokens(int tokenAmount) {
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(EventNames.TOKENS_REQUESTED, new Object[]{correlationId, customer.getDtuPayId(), tokenAmount});
        tokenService.handleTokensRequested(event);
    }

    @Then("a TokensGenerated event with {int} tokens is published")
    public void aTokensGeneratedEventWithTokensIsPublished(int tokenAmount) {
        verify(queueMock).publish(eventCaptor.capture());
        assertEquals(EventNames.TOKENS_GENERATED, eventCaptor.getValue().getType());
        assertEquals(tokenAmount, eventCaptor.getValue().getArgument(1, List.class).size());
    }

    @And("the customer has {int} tokens")
    public void theCustomerHasTokens(int tokenAmount) {
        assertEquals(tokenAmount, customerRepository.getCustomer(customer.getDtuPayId()).getTokens().size());
    }

    @When("a PaymentRequested event is received with a token matching the customers")
    public void aPaymentRequestedEventIsReceivedWithATokenMatchingTheCustomers() {
        CorrelationId correlationId = CorrelationId.randomId();
        Token token = generatedTokens.get(0);
        Event event = new Event(EventNames.PAYMENT_REQUESTED, new Object[]{correlationId, null, token, null});
        tokenService.handlePaymentRequested(event);
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock, timeout(10000)).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
    }

    @When("a CustomerDeregistered event is received")
    public void aCustomerDeregisteredEventIsReceived() {
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(EventNames.CUSTOMER_DEREGISTERED, new Object[]{correlationId, customer.getDtuPayId() });
        tokenService.handleCustomerDeregistered(event);
    }

    @Then("the customer is removed")
    public void theCustomerIsRemoved() {
        assertNull(customerRepository.getCustomer(customer.getDtuPayId()));
    }

    @And("the customer's DTUPay id is returned")
    public void theCustomerSDTUPayIdIsReturned() {
        assertEquals(customer.getDtuPayId(), eventCaptor.getValue().getArgument(1, String.class));
    }
}
