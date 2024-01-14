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
    private static final String PAYMENT_COMPLETED = "PaymentCompleted";
    private final Map<CorrelationId, CompletableFuture<Customer>> customerCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Merchant>> merchantCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<List<Token>>> tokenCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<String>> paymentCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public DTUPayService(MessageQueue q) {
        queue = q;
        queue.addHandler(CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(MERCHANT_REGISTERED, this::handleMerchantRegistered);
        queue.addHandler(TOKENS_GENERATED, this::handleTokensGenerated);
        queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompleted);
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
    private void handleMerchantRegistered(Event event) {
        Merchant merchant = event.getArgument(0, Merchant.class);
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        merchantCorrelations.get(correlationId).complete(merchant);
    }

    public List<Token> requestTokens(String dtuPayId, int tokenAmount) {
        CorrelationId correlationId = CorrelationId.randomId();
        tokenCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(TOKENS_REQUESTED, new Object[]{dtuPayId, tokenAmount, correlationId});
        queue.publish(event);
        List<Token> response = tokenCorrelations.get(correlationId).join();
        tokenCorrelations.remove(correlationId);
        return response;
    }

    private void handleTokensGenerated(Event event) {
        List<Token> tokens = (List<Token>) event.getArgument(0, List.class);
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        tokenCorrelations.get(correlationId).complete(tokens);
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
    private void handlePaymentCompleted(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        paymentCorrelations.get(correlationId).complete("Success");
    }
}
