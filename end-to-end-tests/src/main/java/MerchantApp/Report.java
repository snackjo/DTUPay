package MerchantApp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Peter
@Data
public class Report {
    List<Payment> payments = new ArrayList<>();
}
