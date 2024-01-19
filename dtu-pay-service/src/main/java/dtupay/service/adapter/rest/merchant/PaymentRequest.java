package dtupay.service.adapter.rest.merchant;

import dtupay.service.Token;
import lombok.Data;

// @author Peter
@Data
public class PaymentRequest {
    private Token token;
    private int amount;
}
