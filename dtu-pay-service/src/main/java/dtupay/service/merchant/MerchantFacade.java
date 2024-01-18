package dtupay.service.merchant;

import dtupay.service.CorrelationId;
import dtupay.service.EventNames;
import dtupay.service.Token;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MerchantFacade {
    private final Map<CorrelationId, CompletableFuture<Merchant>> merchantCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<String>> paymentCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Void>> merchantDeregistrationCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public MerchantFacade(MessageQueue q) {
        queue = q;
        queue.addHandler(EventNames.MERCHANT_REGISTERED, this::handleMerchantRegistered);
        queue.addHandler(EventNames.PAYMENT_COMPLETED, this::handlePaymentCompleted);
        queue.addHandler(EventNames.MERCHANT_DEREGISTERED, this::handleMerchantDeregistered);
    }

    public Merchant registerMerchant(Merchant merchant) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(EventNames.MERCHANT_REGISTRATION_REQUESTED, new Object[] { correlationId, merchant });
        queue.publish(event);
        Merchant response = merchantCorrelations.get(correlationId).join();
        merchantCorrelations.remove(correlationId);
        return response;
    }

    public void handleMerchantRegistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Merchant merchant = event.getArgument(1, Merchant.class);
        merchantCorrelations.get(correlationId).complete(merchant);
    }

    public String requestPayment(String merchantDtuPayId, Token token, int amount) {
        CorrelationId correlationId = CorrelationId.randomId();
        paymentCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.PAYMENT_REQUESTED, new Object[]{correlationId, merchantDtuPayId, token, amount});
        queue.publish(event);
        String response = paymentCorrelations.get(correlationId).join();
        paymentCorrelations.remove(correlationId);
        return response;
    }

    public void handlePaymentCompleted(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        paymentCorrelations.get(correlationId).complete("Success");
    }

    public void requestMerchantDeregistration(String merchantDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantDeregistrationCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.MERCHANT_DEREGISTRATION_REQUESTED, new Object[]{correlationId, merchantDtuPayId});
        queue.publish(event);

        merchantDeregistrationCorrelations.get(correlationId).join();
        merchantDeregistrationCorrelations.remove(correlationId);
    }

    public void handleMerchantDeregistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        merchantDeregistrationCorrelations.get(correlationId).complete(null);
    }
}
