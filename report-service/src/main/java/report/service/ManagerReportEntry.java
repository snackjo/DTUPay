package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

// @author Carl
@Data
@AllArgsConstructor
public class ManagerReportEntry {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
    String customerDtuPayId;
}
