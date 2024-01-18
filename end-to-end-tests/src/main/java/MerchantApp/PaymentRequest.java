package MerchantApp;

import lombok.Data;

// @author Carl
@Data
public class PaymentRequest {
    Token token;
    int amount;
}
