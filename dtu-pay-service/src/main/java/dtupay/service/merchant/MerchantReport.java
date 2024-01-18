package dtupay.service.merchant;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MerchantReport {
    List<MerchantReportEntry> payments = new ArrayList<>();
}
