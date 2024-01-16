package dtupay.service;


import messaging.Event;
import messaging.MessageQueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DTUPayService {
    public static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
    public static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    public static final String MERCHANT_REGISTERED = "MerchantRegistered";
    public static final String TOKENS_REQUESTED = "TokensRequested";
    public static final String TOKENS_GENERATED = "TokensGenerated";
    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String TOKENS_REQUEST_REJECTED = "TokensRequestRejected";
    public static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    public static final String MANAGER_REPORT_GENERATED = "ManagerReportGenerated";
    private final Map<CorrelationId, CompletableFuture<Customer>> customerCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Merchant>> merchantCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<ResponseObject<List<Token>>>> tokenCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<String>> paymentCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<List<Payment>>> managerReportCorrelations = new ConcurrentHashMap<>();

    public DTUPayService(MessageQueue q) {
        queue = q;
        queue.addHandler(CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(MERCHANT_REGISTERED, this::handleMerchantRegistered);
        queue.addHandler(TOKENS_GENERATED, this::handleTokensGenerated);
        queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompleted);
        queue.addHandler(TOKENS_REQUEST_REJECTED, this::handleTokensRequestRejected);
        queue.addHandler(MANAGER_REPORT_GENERATED, this::handleManagerReportGenerated);
    }

    public Customer registerCustomer(Customer customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerCorrelations.put(correlationId,new CompletableFuture<>());
        Event event = new Event(CUSTOMER_REGISTRATION_REQUESTED, new Object[] { customer, correlationId });
        queue.publish(event);
        Customer response = customerCorrelations.get(correlationId).join();
        customerCorrelations.remove(correlationId);
        return response;
    }

    public void handleCustomerRegistered(Event event) {
        Customer customer = event.getArgument(0, Customer.class);
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        customerCorrelations.get(correlationId).complete(customer);
    }

    public Merchant registerMerchant(Merchant merchant) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(MERCHANT_REGISTRATION_REQUESTED, new Object[] { merchant, correlationId });
        queue.publish(event);
        Merchant response = merchantCorrelations.get(correlationId).join();
        merchantCorrelations.remove(correlationId);
        return response;
    }
    public void handleMerchantRegistered(Event event) {
        Merchant merchant = event.getArgument(0, Merchant.class);
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        merchantCorrelations.get(correlationId).complete(merchant);
    }

    public List<Token> requestTokens(String dtuPayId, int tokenAmount) throws DTUPayException {
        CorrelationId correlationId = CorrelationId.randomId();
        tokenCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(TOKENS_REQUESTED, new Object[]{dtuPayId, tokenAmount, correlationId});
        queue.publish(event);
        ResponseObject<List<Token>> responseObject = tokenCorrelations.get(correlationId).join();
        tokenCorrelations.remove(correlationId);
        return responseObject.getSuccessContentOrThrow();
    }

    public void handleTokensGenerated(Event event) {
        List<Token> tokens = (List<Token>) event.getArgument(0, List.class);
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        ResponseObject<List<Token>> responseObject = new ResponseObject<>(tokens);
        tokenCorrelations.get(correlationId).complete(responseObject);
    }

    public String requestPayment(String merchantDtuPayId, Token token, int amount) {
        CorrelationId correlationId = CorrelationId.randomId();
        paymentCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(PAYMENT_REQUESTED, new Object[]{merchantDtuPayId, token, amount, correlationId});
        queue.publish(event);
        String response = paymentCorrelations.get(correlationId).join();
        paymentCorrelations.remove(correlationId);
        return response;
    }
    public void handlePaymentCompleted(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        paymentCorrelations.get(correlationId).complete("Success");
    }

    public void handleTokensRequestRejected(Event event) {
        String errorMessage = "Tokens request rejected";
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        ResponseObject<List<Token>> responseObject = new ResponseObject<>(errorMessage);
        tokenCorrelations.get(correlationId).complete(responseObject);
    }

    public List<Payment> requestManagerReport() {
        CorrelationId correlationId = CorrelationId.randomId();
        managerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(MANAGER_REPORT_REQUESTED, new Object[]{correlationId});
        queue.publish(event);

        List<Payment> response = managerReportCorrelations.get(correlationId).join();
        managerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleManagerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        List<Payment> report = (List<Payment>) event.getArgument(1, List.class);
        managerReportCorrelations.get(correlationId).complete(report);
    }
}
