package report.service;

import lombok.AllArgsConstructor;
import lombok.Data;

// @author Oliver
@Data
@AllArgsConstructor
public class MerchantReportEntry {
    Token customerToken;
    int amount;
}
