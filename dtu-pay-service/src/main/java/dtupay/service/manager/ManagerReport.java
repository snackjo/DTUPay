package dtupay.service.manager;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Oliver
@Data
public class ManagerReport {
    List<ManagerReportEntry> payments = new ArrayList<>();
}
