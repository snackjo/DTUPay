package report.service;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Peter
@Data
public class CustomerReport {
    private List<CustomerReportEntry> payments = new ArrayList<>();
}
