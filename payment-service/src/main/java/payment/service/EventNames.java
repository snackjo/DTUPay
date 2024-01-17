package payment.service;

public abstract class EventNames {
    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String CUSTOMER_BANK_ACCOUNT_FOUND = "CustomerBankAccountFound";
    public static final String MERCHANT_BANK_ACCOUNT_FOUND = "MerchantBankAccountFound";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
}
