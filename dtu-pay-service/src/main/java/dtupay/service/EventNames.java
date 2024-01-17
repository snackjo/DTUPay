package dtupay.service;

public abstract class EventNames {
    public static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
    public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
    public static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    public static final String MERCHANT_REGISTERED = "MerchantRegistered";
    public static final String TOKENS_REQUESTED = "TokensRequested";
    public static final String TOKENS_GENERATED = "TokensGenerated";
    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String TOKENS_REQUEST_REJECTED = "TokensRequestRejected";
    public static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    public static final String MANAGER_REPORT_GENERATED = "ManagerReportGenerated";
    public static final String CUSTOMER_REPORT_REQUESTED = "CustomerReportRequested";
    public static final String CUSTOMER_REPORT_GENERATED = "CustomerReportGenerated";
    public static final String MERCHANT_REPORT_REQUESTED = "MerchantReportRequested";
    public static final String MERCHANT_REPORT_GENERATED = "MerchantReportGenerated";
    public static final String MERCHANT_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    public static final String MERCHANT_DEREGISTERED = "MerchantDeregistered";
    public static final String CUSTOMER_DEREGISTRATION_REQUESTED = "CustomerDeregistrationRequested";
    public static final String CUSTOMER_DEREGISTERED = "CustomerDeregistered";
}
