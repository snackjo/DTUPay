package account.service;

// @author Oliver
public abstract class EventNames {
    public static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
    public static final String MERCHANT_REGISTERED = "MerchantRegistered";
    public static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    public static final String TOKEN_MATCH_FOUND = "TokenMatchFound";
    public static final String CUSTOMER_BANK_ACCOUNT_FOUND = "CustomerBankAccountFound";
    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String MERCHANT_BANK_ACCOUNT_FOUND = "MerchantBankAccountFound";
    public static final String MERCHANT_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    public static final String MERCHANT_DEREGISTERED = "MerchantDeregistered";
    public static final String CUSTOMER_DEREGISTRATION_REQUESTED = "CustomerDeregistrationRequested";
    public static final String CUSTOMER_DEREGISTERED = "CustomerDeregistered";
}
