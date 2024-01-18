package dtupay.service.customer;

import dtupay.service.Token;
import lombok.Data;

@Data
public class CustomerReportEntry {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
}
