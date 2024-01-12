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
    }

    public Customer registerCustomer(Customer customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerCorrelations.put(correlationId,new CompletableFuture<>());
        Event event = new Event(CUSTOMER_REGISTRATION_REQUESTED, new Object[] { customer, correlationId });
        queue.publish(event);
        return customerCorrelations.get(correlationId).join();
    }

    public void handleCustomerRegistered(Event event) {
        Customer customer = event.getArgument(0, Customer.class);
        CorrelationId correlationid = event.getArgument(1, CorrelationId.class);
        customerCorrelations.get(correlationid).complete(customer);
        customerCorrelations.remove(correlationid);
    }

    public Merchant registerMerchant(Merchant merchant) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(MERCHANT_REGISTRATION_REQUESTED, new Object[] { merchant, correlationId });
        queue.publish(event);
        return merchantCorrelations.get(correlationId).join();
    }
    private void handleMerchantRegistered(Event event) {
        Merchant merchant = event.getArgument(0, Merchant.class);
        CorrelationId correlationid = event.getArgument(1, CorrelationId.class);
        merchantCorrelations.get(correlationid).complete(merchant);
        merchantCorrelations.remove(correlationid);
    }

    public List<Token> requestTokens(String dtuPayId, int tokenAmount) {
        CorrelationId correlationId = CorrelationId.randomId();
        tokenCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(TOKENS_REQUESTED, new Object[]{dtuPayId, tokenAmount, correlationId});
        queue.publish(event);
        return tokenCorrelations.get(correlationId).join();
    }

    private void handleTokensGenerated(Event event) {
        List<Token> tokens = (List<Token>) event.getArgument(0, List.class);
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        tokenCorrelations.get(correlationId).complete(tokens);
        tokenCorrelations.remove(correlationId);
    }

    public String requestPayment(String merchantDtuPayId, Token token, int amount) {
        CorrelationId correlationId = CorrelationId.randomId();
        paymentCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(PAYMENT_REQUESTED, new Object[]{merchantDtuPayId, token, amount, correlationId});
        queue.publish(event);
        return paymentCorrelations.get(correlationId).join();
    }
}
