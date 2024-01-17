package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantReportEntry {
    Token customerToken;
    int amount;
}
