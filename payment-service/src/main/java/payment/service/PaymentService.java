package payment.service;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String CUSTOMER_BANK_ACCOUNT_FOUND = "CustomerBankAccountFound";
    public static final String MERCHANT_BANK_ACCOUNT_FOUND = "MerchantBankAccountFound";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";

    private final Map<String, PaymentInformation> paymentInformation = new ConcurrentHashMap<>();

    MessageQueue queue;
    private final BankService bank;

    public PaymentService(MessageQueue q, BankService bank) {
        this.queue = q;
        this.bank = bank;

        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
        this.queue.addHandler(CUSTOMER_BANK_ACCOUNT_FOUND, this::handleCustomerBankAccountFound);
        this.queue.addHandler(MERCHANT_BANK_ACCOUNT_FOUND, this::handleMerchantBankAccountFound);

    }

    public void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(3, CorrelationId.class);
        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).setPaymentRequestedEvent(event);
        publishPaymentComplete(correlationId);
    }

    public void handleCustomerBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).setCustomerBankAccountFoundEvent(event);
        publishPaymentComplete(correlationId);
    }

    public void handleMerchantBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(1, CorrelationId.class);
        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).setMerchantBankAccountFoundEvent(event);
        publishPaymentComplete(correlationId);
    }

    private synchronized void createPaymentInformationIfNotExists(CorrelationId correlationId) {
        if (!paymentInformation.containsKey(correlationId.getId())) {
            paymentInformation.put(correlationId.getId(), new PaymentInformation());
        }
    }

    private synchronized void publishPaymentComplete(CorrelationId correlationId) {
        PaymentInformation information = paymentInformation.get(correlationId.getId());
        if (information != null && information.isAllInformationSet()) {
            tryTransferringThroughBank(information);
            Event publishEvent = new Event(PAYMENT_COMPLETED, new Object[] { correlationId });
            queue.publish(publishEvent);
            paymentInformation.remove(correlationId.getId());
        }
    }

    private void tryTransferringThroughBank(PaymentInformation information) {
        try {
            bank.transferMoneyFromTo(information.getCustomerBankAccount(), information.getMerchantBankAccount(), BigDecimal.valueOf(information.getAmount()), "transfer");
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }
}
