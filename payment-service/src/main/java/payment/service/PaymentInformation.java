package payment.service;

import lombok.Data;
import messaging.Event;

@Data
public class PaymentInformation {
    private Event paymentRequestedEvent;
    private Event customerBankAccountFoundEvent;
    private Event merchantBankAccountFoundEvent;

    public boolean isAllInformationSet() {
        return merchantBankAccountFoundEvent != null && customerBankAccountFoundEvent != null && paymentRequestedEvent != null;
    }

    public String getCustomerBankAccount() {
        return customerBankAccountFoundEvent.getArgument(0, String.class);
    }

    public String getMerchantBankAccount() {
        return merchantBankAccountFoundEvent.getArgument(0, String.class);
    }

    public int getAmount() {
        return paymentRequestedEvent.getArgument(2, Integer.class);
    }
}
