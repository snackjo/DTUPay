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

    public String getMerchantDtuPayId() {
        return paymentRequestedEvent.getArgument(1, String.class);
    }

    public Token getCustomerToken() {
        return paymentRequestedEvent.getArgument(2, Token.class);
    }

    public String getCustomerDtuPayId() {
        return customerBankAccountFoundEvent.getArgument(2, String.class);
    }

    public String getCustomerBankAccount() {
        return customerBankAccountFoundEvent.getArgument(1, String.class);
    }

    public String getMerchantBankAccount() {
        return merchantBankAccountFoundEvent.getArgument(1, String.class);
    }

    public int getAmount() {
        return paymentRequestedEvent.getArgument(3, Integer.class);
    }
}
