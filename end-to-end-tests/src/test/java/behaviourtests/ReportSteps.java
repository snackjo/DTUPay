package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import CustomerApp.Token;
import ManagerApp.ManagerClient;
import ManagerApp.Payment;
import ManagerApp.Report;
import MerchantApp.Merchant;
import MerchantApp.MerchantDtuPay;
import Utility.MapperUtility;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReportSteps {
    private final BankService bank = new BankServiceService().getBankServicePort();
    private final MerchantDtuPay merchantDtuPay = new MerchantDtuPay();
    private final CustomerDTUPay customerDtuPay = new CustomerDTUPay();
    private final ManagerClient managerClient = new ManagerClient();
    private Report report = new Report();
    private Payment expectedPayment;

    @Given("a successful payment")
    public void aSuccessfulPayment() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setCprNumber("merchant1-17");
        merchant.setFirstName("firstName1");
        merchant.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(1000);

        String merchantAccountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant), balance);
        merchant.setAccountId(merchantAccountId);

        merchant.setDtuPayId(merchantDtuPay.registerMerchant(merchant).getDtuPayId());

        Customer customer = new Customer();
        customer.setCprNumber("customer1-17");
        customer.setFirstName("firstName1");
        customer.setLastName("lastName1");

        String customerAccountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer), balance);
        customer.setAccountId(customerAccountId);

        customer.setDtuPayId(customerDtuPay.registerCustomer(customer).getDtuPayId());

        List<Token> tokens = customerDtuPay.requestTokens(customer, 5);
        customer.setTokens(tokens);

        Token tokenReceivedFromCustomer = customer.provideToken();

        MerchantApp.Token token = MapperUtility.mapToken(tokenReceivedFromCustomer);
        String paymentResponse = merchantDtuPay.requestPayment(100, token, merchant.getDtuPayId());

        expectedPayment = new Payment();
        expectedPayment.setAmount(100);
        expectedPayment.setCustomerToken(tokenReceivedFromCustomer.toString());
        expectedPayment.setCustomerDtuPayId(customer.getDtuPayId());
        expectedPayment.setMerchantDtuPayId(merchant.getDtuPayId());

        assertEquals("Success", paymentResponse);
    }

    @When("the manager requests a report")
    public void theManagerRequestsAReport() {
        report.setPayments(managerClient.requestReport().getPayments());
    }

    @Then("the report includes the payment")
    public void theReportIncludesThePayment() {
        assertTrue(report.getPayments().contains(expectedPayment));
    }
}
