package dtupay.service;


import messaging.Event;
import messaging.MessageQueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DTUPayService {
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

    private final Map<CorrelationId, CompletableFuture<Customer>> customerCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Merchant>> merchantCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<ResponseObject<List<Token>>>> tokenCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<String>> paymentCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;
    private final Map<CorrelationId, CompletableFuture<Report>> managerReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Report>> merchantReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Report>> customerReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Void>> merchantDeregistrationCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Void>> customerDeregistrationCorrelations = new ConcurrentHashMap<>();

    public DTUPayService(MessageQueue q) {
        queue = q;
        queue.addHandler(CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(MERCHANT_REGISTERED, this::handleMerchantRegistered);
        queue.addHandler(TOKENS_GENERATED, this::handleTokensGenerated);
        queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompleted);
        queue.addHandler(TOKENS_REQUEST_REJECTED, this::handleTokensRequestRejected);
        queue.addHandler(MANAGER_REPORT_GENERATED, this::handleManagerReportGenerated);
        queue.addHandler(CUSTOMER_REPORT_GENERATED, this::handleCustomerReportGenerated);
        queue.addHandler(MERCHANT_REPORT_GENERATED, this::handleMerchantReportGenerated);
        queue.addHandler(MERCHANT_DEREGISTERED, this::handleMerchantDeregistered);
        queue.addHandler(CUSTOMER_DEREGISTERED, this::handleCustomerDeregistered);
    }

    public Customer registerCustomer(Customer customer) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerCorrelations.put(correlationId,new CompletableFuture<>());
        Event event = new Event(CUSTOMER_REGISTRATION_REQUESTED, new Object[] { correlationId, customer });
        queue.publish(event);
        Customer response = customerCorrelations.get(correlationId).join();
        customerCorrelations.remove(correlationId);
        return response;
    }

    public void handleCustomerRegistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Customer customer = event.getArgument(1, Customer.class);
        customerCorrelations.get(correlationId).complete(customer);
    }

    public Merchant registerMerchant(Merchant merchant) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(MERCHANT_REGISTRATION_REQUESTED, new Object[] { correlationId, merchant });
        queue.publish(event);
        Merchant response = merchantCorrelations.get(correlationId).join();
        merchantCorrelations.remove(correlationId);
        return response;
    }
    public void handleMerchantRegistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Merchant merchant = event.getArgument(1, Merchant.class);
        merchantCorrelations.get(correlationId).complete(merchant);
    }

    public List<Token> requestTokens(String dtuPayId, int tokenAmount) throws DTUPayException {
        CorrelationId correlationId = CorrelationId.randomId();
        tokenCorrelations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(TOKENS_REQUESTED, new Object[]{correlationId, dtuPayId, tokenAmount});
        queue.publish(event);
        ResponseObject<List<Token>> responseObject = tokenCorrelations.get(correlationId).join();
        tokenCorrelations.remove(correlationId);
        return responseObject.getSuccessContentOrThrow();
    }

    public void handleTokensGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        List<Token> tokens = (List<Token>) event.getArgument(1, List.class);
        ResponseObject<List<Token>> responseObject = new ResponseObject<>(tokens);
        tokenCorrelations.get(correlationId).complete(responseObject);
    }

    public String requestPayment(String merchantDtuPayId, Token token, int amount) {
        CorrelationId correlationId = CorrelationId.randomId();
        paymentCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(PAYMENT_REQUESTED, new Object[]{correlationId, merchantDtuPayId, token, amount});
        queue.publish(event);
        String response = paymentCorrelations.get(correlationId).join();
        paymentCorrelations.remove(correlationId);
        return response;
    }
    public void handlePaymentCompleted(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        paymentCorrelations.get(correlationId).complete("Success");
    }

    public void handleTokensRequestRejected(Event event) {
        String errorMessage = "Tokens request rejected";
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        ResponseObject<List<Token>> responseObject = new ResponseObject<>(errorMessage);
        tokenCorrelations.get(correlationId).complete(responseObject);
    }

    public Report requestManagerReport() {
        CorrelationId correlationId = CorrelationId.randomId();
        managerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(MANAGER_REPORT_REQUESTED, new Object[]{correlationId});
        queue.publish(event);

        Report response = managerReportCorrelations.get(correlationId).join();
        managerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleManagerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = event.getArgument(1, Report.class);
        managerReportCorrelations.get(correlationId).complete(report);
    }

    public Report requestMerchantReport(String merchantDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(MERCHANT_REPORT_REQUESTED, new Object[]{correlationId, merchantDtuPayId});
        queue.publish(event);

        Report response = merchantReportCorrelations.get(correlationId).join();
        merchantReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleMerchantReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = event.getArgument(1, Report.class);
        merchantReportCorrelations.get(correlationId).complete(report);
    }

    public Report requestCustomerReport(String customerDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(CUSTOMER_REPORT_REQUESTED, new Object[]{correlationId, customerDtuPayId});
        queue.publish(event);

        Report response = customerReportCorrelations.get(correlationId).join();
        customerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleCustomerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = event.getArgument(1, Report.class);
        customerReportCorrelations.get(correlationId).complete(report);
    }

    public void requestMerchantDeregistration(String merchantDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantDeregistrationCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(MERCHANT_DEREGISTRATION_REQUESTED, new Object[]{correlationId, merchantDtuPayId});
        queue.publish(event);

        merchantDeregistrationCorrelations.get(correlationId).join();
        merchantDeregistrationCorrelations.remove(correlationId);
    }

    public void handleMerchantDeregistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        merchantDeregistrationCorrelations.get(correlationId).complete(null);
    }

    public void requestCustomerDeregistration(String customerDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerDeregistrationCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(CUSTOMER_DEREGISTRATION_REQUESTED, new Object[]{correlationId, customerDtuPayId});
        queue.publish(event);

        customerDeregistrationCorrelations.get(correlationId).join();
        customerDeregistrationCorrelations.remove(correlationId);
    }

    public void handleCustomerDeregistered(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        customerDeregistrationCorrelations.get(correlationId).complete(null);
    }
}
