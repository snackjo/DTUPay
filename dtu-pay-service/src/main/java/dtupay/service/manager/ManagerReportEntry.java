package dtupay.service.manager;

import dtupay.service.Token;
import lombok.Data;

// @author Carl
@Data
public class ManagerReportEntry {
    private String merchantDtuPayId;
    private Token customerToken;
    private int amount;
    private String customerDtuPayId;
}
