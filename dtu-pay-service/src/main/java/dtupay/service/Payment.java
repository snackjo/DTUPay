package dtupay.service;

import lombok.Data;

@Data
public class Payment {
    String merchantDtuPayId;
    String customerToken;
    int amount;
    String customerDtuPayId;
}
