package behaviourtests;

import account.service.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
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
    private Customer customer;
    private CorrelationId correlationId;

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

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock).publish(eventCaptor.capture());
        publishedEvent = eventCaptor.getValue();
        assertEquals(eventName, publishedEvent.getType());
    }

    @And("DTUPay id is part of published customer event")
    public void dtupayIdIsPartOfPublishedEvent() {
        assertFalse(publishedEvent.getArgument(0, Customer.class).getDtuPayId().isEmpty());
    }

    @And("the customer is given a non-empty DTUPay id")
    public void theCustomerIsGivenANonEmptyDTUPayId() {
        assertNotNull(accountRepository.getCustomerAccount(publishedEvent.getArgument(0, Customer.class).getDtuPayId()));
    }

    @When("a {string} event for a merchant is received")
    public void aEventForAMerchantIsReceived(String eventName) {
        Merchant merchant = new Merchant();
        merchant.setCprNumber("54321");
        merchant.setFirstName("firstName");
        merchant.setLastName("lastName");
        merchant.setAccountId("bank-id-321");
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[]{merchant, correlationId});

        accountService.handleMerchantRegistrationRequested(event);
    }

    @And("DTUPay id is part of published merchant event")
    public void dtupayIdIsPartOfPublishedMerchantEvent() {
        assertNotNull(accountRepository.getMerchantAccount(publishedEvent.getArgument(0, Merchant.class).getDtuPayId()));
    }

    @And("the merchant is given a non-empty DTUPay id")
    public void theMerchantIsGivenANonEmptyDTUPayId() {
        assertNotNull(accountRepository.getMerchantAccount(publishedEvent.getArgument(0, Merchant.class).getDtuPayId()));
    }

    @Given("a registered customer")
    public void aRegisteredCustomer() {
        customer = new Customer();
        customer.setDtuPayId("987654321");
        customer.setAccountId("123456789");

        accountRepository.addCustomer(customer);
    }

    @When("a {string} event is received with a matching DTUPay id")
    public void aEventIsReceivedWithAMatchingDTUPayId(String eventName) {
        correlationId = CorrelationId.randomId();
        Event event = new Event(eventName, new Object[]{customer.getDtuPayId(), correlationId});

        accountService.handleTokenMatchFound(event);
    }
}
