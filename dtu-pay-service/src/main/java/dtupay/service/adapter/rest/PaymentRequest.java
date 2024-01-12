package dtupay.service.adapter.rest;

import dtupay.service.Token;
import lombok.Data;

@Data
public class PaymentRequest {
    Token token;
    int amount;
}
