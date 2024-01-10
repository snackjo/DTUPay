package behaviourtests;

import account.service.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccountSteps {
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

    private final AccountService accountService = new AccountService(queueMock);
    private final AccountRepository accountRepository = AccountRepositoryFactory.getRepository();
    private Event publishedEvent;

    @When("a {string} event for a customer is received")
    public void aEventForACustomerIsReceived(String eventName) {
        Customer customer = new Customer();
        customer.setCprNumber("12345");
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        customer.setAccountId("bank-id-123");
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[]{customer, correlationId});

        accountService.handleCustomerRegistrationRequested(event);
    }

    @Then("a {string} event is published with DTUPay id")
    public void aEventIsPublished(String eventName) {
        verify(queueMock).publish(eventCaptor.capture());
        publishedEvent = eventCaptor.getValue();
        assertEquals(eventName, publishedEvent.getType());
        assertFalse(publishedEvent.getArgument(0, Customer.class).getDtuPayId().isEmpty());
    }

    @And("the customer is given a non-empty DTUPay id")
    public void theCustomerIsGivenANonEmptyDTUPayId() {
        assertNotNull(accountRepository.getCustomerAccount(publishedEvent.getArgument(0, Customer.class).getDtuPayId()));
    }
}
