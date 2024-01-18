package dtupay.service.merchant;

import dtupay.service.Token;
import lombok.Data;

// @author Emil
@Data
public class MerchantReportEntry {
    Token customerToken;
    int amount;
}
