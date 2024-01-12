package dtupay.service;

import lombok.Data;

@Data
public class PaymentRequest {
    Token token;
    int amount;
}
