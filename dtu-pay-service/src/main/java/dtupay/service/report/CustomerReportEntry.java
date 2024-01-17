package dtupay.service.report;

import dtupay.service.Token;
import lombok.Data;

@Data
public class CustomerReportEntry {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
}
