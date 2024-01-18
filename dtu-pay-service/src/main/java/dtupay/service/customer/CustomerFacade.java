package dtupay.service.customer;

import dtupay.service.CorrelationId;
import dtupay.service.DtuPayException;
import dtupay.service.EventNames;
import dtupay.service.Token;
import messaging.Event;
import messaging.MessageQueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerFacade {
    private final Map<CorrelationId, CompletableFuture<Customer>> customerCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<ResponseObject<List<Token>>>> tokenCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Void>> customerDeregistrationCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<CustomerReport>> customerReportCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public CustomerFacade(MessageQueue q) {
        queue = q;
        queue.addHandler(EventNames.CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(EventNames.TOKENS_GENERATED, this::handleTokensGenerated);
        queue.addHandler(EventNames.TOKENS_REQUEST_REJECTED, this::handleTokensRequestRejected);
        queue.addHandler(EventNames.CUSTOMER_DEREGISTERED, this::handleCustomerDeregistered);
        queue.addHandler(EventNames.CUSTOMER_REPORT_GENERATED, this::handleCustomerReportGenerated);
    }

    public Customer registerCustomer(Customer customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(EventNames.CUSTOMER_REGISTRATION_REQUESTED, new Object[]{correlationId, customer});
        queue.publish(event);
        Customer response = customerCorrelations.get(correlationId).join();
        customerCorrelations.remove(correlationId);
        return response;
    }

    public void handleCustomerRegistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Customer customer = event.getArgument(1, Customer.class);
        customerCorrelations.get(correlationId).complete(customer);
    }

    public List<Token> requestTokens(String dtuPayId, int tokenAmount) throws DtuPayException {
        CorrelationId correlationId = CorrelationId.randomId();
        tokenCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(EventNames.TOKENS_REQUESTED, new Object[]{correlationId, dtuPayId, tokenAmount});
        queue.publish(event);
        ResponseObject<List<Token>> responseObject = tokenCorrelations.get(correlationId).join();
        tokenCorrelations.remove(correlationId);
        return responseObject.getSuccessContentOrThrow();
    }

    public void handleTokensGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        List<Token> tokens = (List<Token>) event.getArgument(1, List.class);
        ResponseObject<List<Token>> responseObject = new ResponseObject<>(tokens);
        tokenCorrelations.get(correlationId).complete(responseObject);
    }

    public void handleTokensRequestRejected(Event event) {
        String errorMessage = "Tokens request rejected";
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        ResponseObject<List<Token>> responseObject = new ResponseObject<>(errorMessage);
        tokenCorrelations.get(correlationId).complete(responseObject);
    }

    public void requestCustomerDeregistration(String customerDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerDeregistrationCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.CUSTOMER_DEREGISTRATION_REQUESTED, new Object[]{correlationId, customerDtuPayId});
        queue.publish(event);

        customerDeregistrationCorrelations.get(correlationId).join();
        customerDeregistrationCorrelations.remove(correlationId);
    }

    public void handleCustomerDeregistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        customerDeregistrationCorrelations.get(correlationId).complete(null);
    }

    public CustomerReport requestCustomerReport(String customerDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.CUSTOMER_REPORT_REQUESTED, new Object[]{correlationId, customerDtuPayId});
        queue.publish(event);

        CustomerReport response = customerReportCorrelations.get(correlationId).join();
        customerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleCustomerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        CustomerReport report = event.getArgument(1, CustomerReport.class);
        customerReportCorrelations.get(correlationId).complete(report);
    }
}
