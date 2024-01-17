package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerReportEntry {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
}
