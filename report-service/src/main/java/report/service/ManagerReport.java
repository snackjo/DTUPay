package report.service;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Emil
@Data
public class ManagerReport {
    private List<ManagerReportEntry> payments = new ArrayList<>();
}
