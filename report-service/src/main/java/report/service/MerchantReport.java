package report.service;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Bastian
@Data
public class MerchantReport {
    private List<MerchantReportEntry> payments = new ArrayList<>();
}
