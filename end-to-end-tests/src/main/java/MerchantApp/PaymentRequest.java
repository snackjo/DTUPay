package MerchantApp;

import lombok.Data;

@Data
public class PaymentRequest {
    Token token;
    int amount;
}
