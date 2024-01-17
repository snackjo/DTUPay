package dtupay.service.merchant;

import dtupay.service.Token;
import lombok.Data;

@Data
public class PaymentRequest {
    Token token;
    int amount;
}
