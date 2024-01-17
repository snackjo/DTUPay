package CustomerApp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Report {
    List<Payment> payments = new ArrayList<>();
}
