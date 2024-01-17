package ManagerApp;

import lombok.Data;

@Data
public class Payment {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
    String customerDtuPayId;
}
