package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

// @author Oliver
@Data
@AllArgsConstructor
public class CustomerReportEntry {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
}
