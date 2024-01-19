package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

// @author Carl
@Data
@AllArgsConstructor
public class ManagerReportEntry {
    private String merchantDtuPayId;
    private Token customerToken;
    private int amount;
    private String customerDtuPayId;
}
