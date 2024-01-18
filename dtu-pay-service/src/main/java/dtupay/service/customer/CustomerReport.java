package dtupay.service.customer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerReport {
    List<CustomerReportEntry> payments = new ArrayList<>();
}
