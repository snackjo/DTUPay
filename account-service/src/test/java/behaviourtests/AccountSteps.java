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
import static org.mockito.Mockito.*;

public class AccountSteps {
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final AccountRepository accountRepository = new AccountRepository();
    private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    private final AccountService accountService = new AccountService(queueMock, accountRepository);
    private Event publishedEvent;
    private Customer customer;
    private CorrelationId correlationId;
    private Merchant merchant;

    @When("a CustomerRegistrationRequested event is received")
    public void aCustomerRegistrationRequestedEventIsReceived() {
        Customer customer = new Customer();
        customer.setCprNumber("12345");
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        customer.setAccountId("bank-id-123");
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(AccountService.CUSTOMER_REGISTRATION_REQUESTED, new Object[]{correlationId, customer});

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
        assertFalse(publishedEvent.getArgument(1, Customer.class).getDtuPayId().isEmpty());
    }

    @And("the customer is given a non-empty DTUPay id")
    public void theCustomerIsGivenANonEmptyDTUPayId() throws DtuPayException {
        assertNotNull(accountRepository.getCustomerAccount(publishedEvent.getArgument(1, Customer.class).getDtuPayId()));
    }

    @When("a MerchantRegistrationRequested event is received")
    public void aMerchantRegistrationRequestedEventIsReceived() {
        Merchant merchant = new Merchant();
        merchant.setCprNumber("54321");
        merchant.setFirstName("firstName");
        merchant.setLastName("lastName");
        merchant.setAccountId("bank-id-321");
        CorrelationId correlationId = CorrelationId.randomId();
        Event event = new Event(AccountService.MERCHANT_REGISTRATION_REQUESTED, new Object[]{correlationId, merchant});

        accountService.handleMerchantRegistrationRequested(event);
    }

    @And("DTUPay id is part of published merchant event")
    public void dtupayIdIsPartOfPublishedMerchantEvent() throws DtuPayException {
        assertNotNull(accountRepository.getMerchantAccount(publishedEvent.getArgument(1, Merchant.class).getDtuPayId()));
    }

    @And("the merchant is given a non-empty DTUPay id")
    public void theMerchantIsGivenANonEmptyDTUPayId() throws DtuPayException {
        assertNotNull(accountRepository.getMerchantAccount(publishedEvent.getArgument(1, Merchant.class).getDtuPayId()));
    }

    @Given("a registered customer")
    public void aRegisteredCustomer() {
        customer = new Customer();
        customer.setDtuPayId("987654321");
        customer.setAccountId("123456789");

        accountRepository.addCustomer(customer);
    }

    @When("a TokenMatchFound event is received")
    public void aTokenMatchFoundEventIsReceived() {
        correlationId = CorrelationId.randomId();
        Event event = new Event(AccountService.TOKEN_MATCH_FOUND, new Object[]{correlationId, customer.getDtuPayId()});

        accountService.handleTokenMatchFound(event);
    }

    @When("a PaymentRequested event is received")
    public void aPaymentRequestedEventIsReceived() {
        correlationId = CorrelationId.randomId();
        Event event = new Event(AccountService.PAYMENT_REQUESTED, new Object[]{correlationId, merchant.getDtuPayId(), null, null});

        accountService.handlePaymentRequested(event);
    }

    @Given("a registered merchant")
    public void aRegisteredMerchant() {
        merchant = new Merchant();
        merchant.setDtuPayId("56787654");
        merchant.setAccountId("9876567");

        accountRepository.addMerchant(merchant);
    }

    @And("the published customer account id is correct")
    public void thePublishedCustomerAccountIdIsCorrect() {
        assertEquals(customer.getAccountId(), eventCaptor.getValue().getArgument(1, String.class));
    }

    @And("the published merchant account id is correct")
    public void thePublishedMerchantAccountIdIsCorrect() {
        assertEquals(merchant.getAccountId(), eventCaptor.getValue().getArgument(1, String.class));
    }

    @And("the customer DTUPay id is also in the event")
    public void theCustomerDTUPayIdIsAlsoInTheEvent() {
        assertEquals(customer.getDtuPayId(), eventCaptor.getValue().getArgument(2, String.class));
    }

    @When("a MerchantDeregistrationRequested event is received")
    public void aMerchantDeregistrationRequestedEventIsReceived() {
        correlationId = CorrelationId.randomId();
        Event event = new Event(AccountService.MERCHANT_DEREGISTRATION_REQUESTED, new Object[]{correlationId, merchant.getDtuPayId()});
        accountService.handleMerchantDeregistrationRequested(event);
    }

    @And("the merchant's account is removed")
    public void theMerchantSAccountIsRemoved() {
        DtuPayException exception = new DtuPayException("Placeholder");
        try {
            accountRepository.getMerchantAccount(merchant.getDtuPayId());
        } catch (DtuPayException e) {
            exception = e;
        }
        assertEquals("Merchant is not registered", exception.getMessage());
    }

    @When("a CustomerDeregistrationRequested event is received")
    public void aCustomerDeregistrationRequestedEventIsReceived() {
        correlationId = CorrelationId.randomId();
        Event event = new Event(AccountService.CUSTOMER_DEREGISTRATION_REQUESTED, new Object[]{correlationId, customer.getDtuPayId()});
        accountService.handleCustomerDeregistrationRequested(event);
    }

    @And("the customer's account is removed")
    public void theCustomerSAccountIsRemoved() {
        DtuPayException exception = new DtuPayException("Placeholder");
        try {
            accountRepository.getCustomerAccount(customer.getDtuPayId());
        } catch (DtuPayException e) {
            exception = e;
        }
        assertEquals("Customer is not registered", exception.getMessage());
    }
}
