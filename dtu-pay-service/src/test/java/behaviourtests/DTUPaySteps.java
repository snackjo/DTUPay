package behaviourtests;

import dtupay.service.CorrelationId;
import dtupay.service.Customer;
import dtupay.service.DTUPayService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class DTUPaySteps {
    Customer customer;
    private final Map<String, CompletableFuture<Event>> publishedEvents = new HashMap<>();
    private final Map<Customer, CorrelationId> correlationIds = new HashMap<>();

    private final MessageQueue q = new MessageQueue() {
        @Override
        public void publish(Event event) {
            Customer customerFromEvent = event.getArgument(0, Customer.class);
            publishedEvents.get(customerFromEvent.getFirstName()).complete(event);
        }

        @Override
        public void addHandler(String eventType, Consumer<Event> handler) {
        }

    };
    private final DTUPayService service = new DTUPayService(q);
    private final CompletableFuture<Customer> registeredCustomer = new CompletableFuture<>();

    @Given("a customer with empty DTUPay id")
    public void aCustomerWithEmptyDTUPayId() {
        customer = new Customer();
        customer.setCprNumber("11111111-17");
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        publishedEvents.put(customer.getFirstName(), new CompletableFuture<Event>());
        assertNull(customer.getDtuPayId());
    }

    @When("the customer is being registered")
    public void theCustomerIsBeingRegistered() {
        new Thread(() -> {
            Customer registrationResult = service.registerCustomer(customer);
            registeredCustomer.complete(registrationResult);
        }).start();
    }

    @Then("a {string} event is published")
    public void aEventIsPublished(String eventName) {
        Event publishedEvent = publishedEvents.get(customer.getFirstName()).join();
        assertEquals(eventName, publishedEvent.getType());
        Customer customer = publishedEvent.getArgument(0, Customer.class);
        CorrelationId correlationId = publishedEvent.getArgument(1, CorrelationId.class);
        correlationIds.put(customer, correlationId);
    }

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        Customer customer = new Customer();
        customer.setFirstName(this.customer.getFirstName());
        customer.setDtuPayId("123");
        service.handleCustomerRegistered(new Event(eventName,
                new Object[] {customer, correlationIds.get(this.customer)}));
    }

    @Then("the customer is registered and his DTUPay id is set")
    public void theCustomerIsRegisteredAndHisDTUPayIdIsSet() {
        assertNotNull(registeredCustomer.join().getDtuPayId());
    }
}
