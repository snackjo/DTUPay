package payment.service;

import lombok.Data;
import messaging.Event;

@Data
public class PaymentInformation {
    Event paymentRequestedEvent;
    Event customerBankAccountFoundEvent;
    Event merchantBankAccountFoundEvent;
}
