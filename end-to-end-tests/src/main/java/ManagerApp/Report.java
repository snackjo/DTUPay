package ManagerApp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Carl
@Data
public class Report {
    List<Payment> payments = new ArrayList<>();
}
