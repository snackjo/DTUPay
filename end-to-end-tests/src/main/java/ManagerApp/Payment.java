package ManagerApp;

import lombok.Data;

// @author Oliver
@Data
public class Payment {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
    String customerDtuPayId;
}
