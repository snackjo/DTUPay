package report.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// @author Carl
public class ReportRepository {
    private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());

    public void storePayment(Payment payment) {
        payments.add(payment);
    }

    public ManagerReport getManagerReport() {
        List<ManagerReportEntry> entries = payments.stream()
                .map(payment -> new ManagerReportEntry(payment.getMerchantDtuPayId(), payment.getCustomerToken(), payment.getAmount(), payment.getCustomerDtuPayId()))
                .collect(Collectors.toList());
        ManagerReport report = new ManagerReport();
        report.setPayments(entries);
        return report;
    }

    public CustomerReport getCustomerReport(String customerDtuPayId) {
        List<CustomerReportEntry> entries = payments.stream()
                .filter(payment -> payment.getCustomerDtuPayId().equals(customerDtuPayId))
                .map(payment -> new CustomerReportEntry(payment.getMerchantDtuPayId(), payment.getCustomerToken(), payment.getAmount()))
                .collect(Collectors.toList());
        CustomerReport report = new CustomerReport();
        report.setPayments(entries);
        return report;
    }

    public MerchantReport getMerchantReport(String merchantDtuPayId) {
        List<MerchantReportEntry> entries = payments.stream()
                .filter(payment -> payment.getMerchantDtuPayId().equals(merchantDtuPayId))
                .map(payment -> new MerchantReportEntry(payment.getCustomerToken(), payment.getAmount()))
                .collect(Collectors.toList());
        MerchantReport report = new MerchantReport();
        report.setPayments(entries);
        return report;
    }
}
