package payment.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.List;

public class PaymentService {

    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String CUSTOMER_BANK_ACCOUNT_FOUND = "CustomerBankAccountFound";
    public static final String MERCHANT_BANK_ACCOUNT_FOUND = "MerchantBankAccountFound";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";


    private final EventRepository eventRepository;


    MessageQueue queue;

    public PaymentService(MessageQueue q) {
        this.queue = q;
        eventRepository = EventRepositoryFactory.getRepository();

        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
        this.queue.addHandler(CUSTOMER_BANK_ACCOUNT_FOUND, this::handleCustomerBankAccountFound);
        this.queue.addHandler(MERCHANT_BANK_ACCOUNT_FOUND, this::handleMerchantBankAccountFound);

    }

    public void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(3, CorrelationId.class);
        eventRepository.putEvent(correlationId, event);
        publishPaymentComplete(correlationId);
    }

    public void handleCustomerBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        eventRepository.putEvent(correlationId, event);
        publishPaymentComplete(correlationId);
    }

    public void handleMerchantBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        eventRepository.putEvent(correlationId, event);
        publishPaymentComplete(correlationId);
    }

    private void publishPaymentComplete(CorrelationId correlationId) {
        if (containsAllEvents(correlationId)) {
            Event publishEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId });
            queue.publish(publishEvent);
        }
    }

    public boolean containsAllEvents(CorrelationId correlationId) {
        List<Event> events = eventRepository.getEvents(correlationId);
        List<String> eventNames = List.of(PAYMENT_REQUESTED, CUSTOMER_BANK_ACCOUNT_FOUND, MERCHANT_BANK_ACCOUNT_FOUND);
        if (events == null || events.isEmpty()) {
            return false;
        }

        return eventNames.stream().allMatch(eventName ->
                events.stream().anyMatch(event -> event.getType().equals(eventName)));
    }
}
