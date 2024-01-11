package dtupay.service;


import messaging.Event;
import messaging.MessageQueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DtuPayService {
    public static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
    private static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    private static final String MERCHANT_REGISTERED = "MerchantRegistered";
    private static final String TOKENS_REQUESTED = "TokensRequested";
    private static final String TOKENS_GENERATED = "TokensGenerated";
    private final Map<CorrelationId, CompletableFuture<Customer>> customerCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Merchant>> merchantCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<List<Token>>> tokenCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public DtuPayService(MessageQueue q) {
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

    private void handleCustomerRegistered(Event event) {
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
}
