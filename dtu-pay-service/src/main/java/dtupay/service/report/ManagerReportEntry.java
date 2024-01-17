package dtupay.service.report;

import dtupay.service.Token;
import lombok.Data;

@Data
public class ManagerReportEntry {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
    String customerDtuPayId;
}