package report.service;

import lombok.*;

// @author Peter
@Data
@NoArgsConstructor
public class Payment {
    private String merchantDtuPayId;
    private Token customerToken;
    private int amount;
    private String customerDtuPayId;
}
