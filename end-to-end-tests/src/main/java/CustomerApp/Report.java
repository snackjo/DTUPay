package CustomerApp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// @author Emil
@Data
public class Report {
    List<Payment> payments = new ArrayList<>();
}
