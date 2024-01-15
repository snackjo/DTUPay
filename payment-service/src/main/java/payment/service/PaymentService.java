package payment.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String CUSTOMER_BANK_ACCOUNT_FOUND = "CustomerBankAccountFound";
    public static final String MERCHANT_BANK_ACCOUNT_FOUND = "MerchantBankAccountFound";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";

    private final Map<String, PaymentInformation> paymentInformation = new ConcurrentHashMap<>();


    MessageQueue queue;

    public PaymentService(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
        this.queue.addHandler(CUSTOMER_BANK_ACCOUNT_FOUND, this::handleCustomerBankAccountFound);
        this.queue.addHandler(MERCHANT_BANK_ACCOUNT_FOUND, this::handleMerchantBankAccountFound);

    }

    public synchronized void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(3, CorrelationId.class);
        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).paymentRequestedEvent = event;
        publishPaymentComplete(correlationId);
    }

    public synchronized void handleCustomerBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).customerBankAccountFoundEvent = event;
        publishPaymentComplete(correlationId);
    }

    public synchronized void handleMerchantBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).merchantBankAccountFoundEvent = event;
        publishPaymentComplete(correlationId);
    }

    private void createPaymentInformationIfNotExists(CorrelationId correlationId) {
        if (!paymentInformation.containsKey(correlationId.getId())) {
            paymentInformation.put(correlationId.getId(), new PaymentInformation());
        }
    }

    private void publishPaymentComplete(CorrelationId correlationId) {
        PaymentInformation information = paymentInformation.get(correlationId.getId());
        if (information.merchantBankAccountFoundEvent != null && information.customerBankAccountFoundEvent != null && information.paymentRequestedEvent != null) {
            Event publishEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId });
            queue.publish(publishEvent);
        }
    }
}
