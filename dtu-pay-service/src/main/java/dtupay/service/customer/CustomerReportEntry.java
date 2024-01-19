package dtupay.service.customer;

import dtupay.service.Token;
import lombok.Data;

// @author Emil
@Data
public class CustomerReportEntry {
    private String merchantDtuPayId;
    private Token customerToken;
    private int amount;
}
