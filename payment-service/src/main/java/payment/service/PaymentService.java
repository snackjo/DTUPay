package payment.service;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// @author Emil
public class PaymentService {

    private final Map<String, PaymentInformation> paymentInformation = new ConcurrentHashMap<>();
    private final BankService bank;
    private final MessageQueue queue;

    public PaymentService(MessageQueue q, BankService bank) {
        this.queue = q;
        this.bank = bank;

        this.queue.addHandler(EventNames.PAYMENT_REQUESTED, this::handlePaymentRequested);
        this.queue.addHandler(EventNames.CUSTOMER_BANK_ACCOUNT_FOUND, this::handleCustomerBankAccountFound);
        this.queue.addHandler(EventNames.MERCHANT_BANK_ACCOUNT_FOUND, this::handleMerchantBankAccountFound);

    }

    public void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).setPaymentRequestedEvent(event);

        transferMoneyThroughBank(correlationId);
    }

    public void handleCustomerBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).setCustomerBankAccountFoundEvent(event);

        transferMoneyThroughBank(correlationId);
    }

    public void handleMerchantBankAccountFound(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        createPaymentInformationIfNotExists(correlationId);
        paymentInformation.get(correlationId.getId()).setMerchantBankAccountFoundEvent(event);

        transferMoneyThroughBank(correlationId);
    }

    private synchronized void createPaymentInformationIfNotExists(CorrelationId correlationId) {
        if (!paymentInformation.containsKey(correlationId.getId())) {
            paymentInformation.put(correlationId.getId(), new PaymentInformation());
        }
    }

    private synchronized void transferMoneyThroughBank(CorrelationId correlationId) {
        PaymentInformation information = paymentInformation.get(correlationId.getId());
        if (information != null && information.isAllInformationSet()) {
            tryTransferringThroughBank(information);
            Event publishEvent = new Event(EventNames.PAYMENT_COMPLETED,
                    new Object[]{
                            correlationId,
                            information.getMerchantDtuPayId(),
                            information.getCustomerToken(),
                            information.getAmount(),
                            information.getCustomerDtuPayId()
                    });
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
