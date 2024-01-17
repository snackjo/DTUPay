package dtupay.service.report;

import dtupay.service.Token;
import lombok.Data;

@Data
public class Payment {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
    String customerDtuPayId;
}
