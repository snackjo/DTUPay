package behaviourtests;

import dtupay.service.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private Event tokensRequestedEvent;
    private List<Token> tokensGenerated;
    private Thread tokenRequestThread;

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
        new Thread(() -> customerRegistrationResult = service.registerCustomer(customer)).start();
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        verify(queueMock, timeout(10000)).publish(eventCaptor.capture());
        tokensRequestedEvent = eventCaptor.getValue();

        assertEquals(eventName, tokensRequestedEvent.getType());

        correlationId = getCorrelationId(tokensRequestedEvent);
    }
    
    @When("a CustomerRegistered event is received")
    public void aCustomerRegisteredEventIsReceived() {
        Customer customer = new Customer();
        customer.setDtuPayId("123");
        service.handleCustomerRegistered(new Event(DTUPayService.CUSTOMER_REGISTERED,
                new Object[] {customer, correlationId}));
    }

    @Then("the customer is registered and his DTUPay id is set")
    public void theCustomerIsRegisteredAndHisDTUPayIdIsSet() {
        assertNotNull(customerRegistrationResult.getDtuPayId());
    }

    @When("a payment of {int} is being requested")
    public void aPaymentOfIsBeingRequested(int paymentAmount) {
        String merchantDtuPayId = "12345";
        Token token = new Token();
        token.setId("abcd");

        new Thread(() -> paymentCompletedResponse = service.requestPayment(merchantDtuPayId, token, paymentAmount)).start();
    }

    private CorrelationId getCorrelationId(Event event) {
        Optional<CorrelationId> firstCorrelationId = Arrays.stream(event.getArguments())
                .filter(CorrelationId.class::isInstance)
                .map(CorrelationId.class::cast)
                .findFirst();

        return firstCorrelationId.orElse(null);
    }

    @When("a TokensRequestRejected event is received")
    public void aTokensRequestRejectedEventIsReceived() {
        service.handleTokensRequestRejected(new Event(DTUPayService.TOKENS_REQUEST_REJECTED,
                new Object[] {correlationId}));
    }

    @Then("a DTUPay exception is thrown")
    public void aDTUPayExceptionIsThrown() {
        assertNotNull(tokenRequestException);
    }

    @When("a customer requests {int} tokens")
    public void theCustomerRequestsTokens(int tokenAmount) {
        tokenRequestThread = new Thread(() -> {
            try{
                tokensGenerated = service.requestTokens("DTUPayId", tokenAmount);
            }catch(DTUPayException e){
                tokenRequestException = e;
            }
        });
        tokenRequestThread.start();

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
        new Thread(() -> merchantRegistrationResult = service.registerMerchant(merchant)).start();
    }

    @When("a MerchantRegistered event is received")
    public void aMerchantRegisteredEventIsReceived() {
        Merchant merchant = new Merchant();
        merchant.setDtuPayId("123");
        service.handleMerchantRegistered(new Event(DTUPayService.MERCHANT_REGISTERED,
                new Object[] {merchant, correlationId}));
    }

    @Then("the merchant is registered and his DTUPay id is set")
    public void theMerchantIsRegisteredAndHisDTUPayIdIsSet() {
        assertNotNull(merchantRegistrationResult.getDtuPayId());
    }

    @When("a PaymentCompleted event is received")
    public void aPaymentCompletedEventIsReceived() {
        service.handlePaymentCompleted(new Event(DTUPayService.PAYMENT_COMPLETED,
                new Object[] {correlationId}));
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals("Success", paymentCompletedResponse);
    }

    @When("a TokensGenerated event is received")
    public void aTokensGeneratedEventIsReceived() {
        List<Token> tokens = new ArrayList<>();
        int requestedAmount = tokensRequestedEvent.getArgument(1, Integer.class);
        for(int i = 0; i < requestedAmount; i++){
            Token token = new Token();
            token.setId(String.valueOf(i));
            tokens.add(token);
        }
        service.handleTokensGenerated(new Event(DTUPayService.TOKENS_GENERATED,
                new Object[]{tokens, correlationId}));
    }

    @Then("{int} tokens are returned")
    public void tokensAreReturned(int tokenAmount) throws InterruptedException {
        tokenRequestThread.join();
        assertEquals(tokenAmount, tokensGenerated.size());
    }
}
