package dtupay.service.manager;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Oliver
@Data
public class ManagerReport {
    private List<ManagerReportEntry> payments = new ArrayList<>();
}
