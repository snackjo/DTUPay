package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

// @author Oliver
@Data
@AllArgsConstructor
public class CustomerReportEntry {
    private String merchantDtuPayId;
    private Token customerToken;
    private int amount;
}
