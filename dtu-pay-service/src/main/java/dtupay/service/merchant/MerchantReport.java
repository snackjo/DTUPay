package dtupay.service.merchant;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Peter
@Data
public class MerchantReport {
    List<MerchantReportEntry> payments = new ArrayList<>();
}
