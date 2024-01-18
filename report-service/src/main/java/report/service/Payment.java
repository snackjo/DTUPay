package report.service;

import lombok.*;

// @author Peter
@Data
@NoArgsConstructor
public class Payment {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
    String customerDtuPayId;
}
