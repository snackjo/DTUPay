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
    private final TokenService tokenService = new TokenService(queueMock);
    private final CustomerRepository customerRepository = CustomerRepositoryFactory.getRepository();

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        customer = new Customer();
        customer.setDtuPayId("cid1");

        Event event = new Event(eventName, new Object[]{customer});
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
        customerRepository.addCustomer(customer);
        assertEquals(tokenAmount, customer.getTokens().size());
    }

    @When("a {string} event for a customer is received for {int} tokens")
    public void aEventForACustomerIsReceivedForTokens(String eventName, int tokenAmount) {
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[]{customer.getDtuPayId(), tokenAmount, correlationId});
        tokenService.handleTokensRequested(event);
    }

    @Then("a {string} event with {int} tokens is published")
    public void aEventWithTokensIsPublished(String eventName, int tokenAmount) {
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(queueMock).publish(eventCaptor.capture());
        assertEquals(eventName, eventCaptor.getValue().getType());
        assertEquals(tokenAmount, eventCaptor.getValue().getArgument(0, List.class).size());
    }

    @And("the customer has {int} tokens")
    public void theCustomerHasTokens(int tokenAmount) {
        assertEquals(tokenAmount, customerRepository.getCustomer(customer.getDtuPayId()).getTokens().size());
    }
}
