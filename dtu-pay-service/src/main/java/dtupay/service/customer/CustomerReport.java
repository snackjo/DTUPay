package dtupay.service.customer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Bastian
@Data
public class CustomerReport {
    private List<CustomerReportEntry> payments = new ArrayList<>();
}
