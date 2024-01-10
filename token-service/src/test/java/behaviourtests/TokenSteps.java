package behaviourtests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import token.service.Customer;
import token.service.CustomerRepository;
import token.service.CustomerRepositoryFactory;
import token.service.TokenService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TokenSteps {
    private Customer customer;
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final TokenService tokenService = new TokenService(queueMock);
    private final CustomerRepository customerRepository = CustomerRepositoryFactory.getRepository();

    @Given("a registered customer with {int} tokens")
    public void aRegisteredCustomerWithTokens(int tokenAmount) {
        customer = new Customer();
        customer.setDtuPayId("cid1");
        customerRepository.addCustomer(customer);
        assertEquals(tokenAmount, customer.getTokens().size());
    }

    @When("a {string} event for a customer is received for {int} tokens")
    public void aEventForACustomerIsReceivedForTokens(String eventName, int tokenAmount) {
        Event event = new Event(eventName, new Object[]{customer, tokenAmount});
        tokenService.handleTokensRequested(event);
    }

    @Then("a {string} event with {int} tokens is published")
    public void aEventWithTokensIsPublished(String eventName, int tokenAmount) {
        verify(queueMock).publish(argThat((Event event) ->
                event.getType().equals(eventName) &&
                        event.getArgument(0, Customer.class).getTokens().size() == tokenAmount));
    }

    @And("the customer has {int} tokens")
    public void theCustomerHasTokens(int tokenAmount) {
        assertEquals(tokenAmount, customerRepository.getCustomer(customer.getDtuPayId()).getTokens().size());
    }
}
