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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TokenSteps {
    private Customer customer;
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final TokenService tokenService = new TokenService(queueMock, customerRepository);
    private List<Token> generatedTokens;
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        customer = new Customer();
        customer.setDtuPayId("cid1");
        CorrelationId correlationId = CorrelationId.randomId();

        Event event = new Event(eventName, new Object[]{correlationId, customer});
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

    @When("a {string} event for a customer is received for {int} tokens")
    public void aEventForACustomerIsReceivedForTokens(String eventName, int tokenAmount) {
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[]{correlationId, customer.getDtuPayId(), tokenAmount});
        tokenService.handleTokensRequested(event);
    }

    @Then("a {string} event with {int} tokens is published")
    public void aEventWithTokensIsPublished(String eventName, int tokenAmount) {
        verify(queueMock).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
        assertEquals(tokenAmount, eventCaptor.getValue().getArgument(1, List.class).size());
    }

    @And("the customer has {int} tokens")
    public void theCustomerHasTokens(int tokenAmount) {
        assertEquals(tokenAmount, customerRepository.getCustomer(customer.getDtuPayId()).getTokens().size());
    }

    @When("a {string} event is received with a token matching the customers")
    public void aEventIsReceivedWithATokenMatchingTheCustomers(String eventName) {
        CorrelationId correlationId = CorrelationId.randomId();
        Token token = generatedTokens.get(0);
        Event event = new Event(eventName, new Object[]{correlationId, null, token, null});
        tokenService.handlePaymentRequested(event);
    }

    @Then("a {string} event is published with the customer's DTUPay id")
    public void aEventIsPublishedWithTheCustomersDTUPayId(String eventName) {
        verify(queueMock).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
        assertEquals(customer.getDtuPayId(), eventCaptor.getValue().getArgument(1, String.class));
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
    }
}
