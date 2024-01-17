package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDtuPay;
import ManagerApp.ManagerDtuPay;
import MerchantApp.Merchant;
import MerchantApp.MerchantDtuPay;
import Utility.MapperUtility;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
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
    private final CustomerDtuPay customerDtuPay = new CustomerDtuPay();
    private final ManagerDtuPay managerDtuPay = new ManagerDtuPay();
    private final ManagerApp.Report managerReport = new ManagerApp.Report();
    private final CustomerApp.Report customerReport = new CustomerApp.Report();
    private final MerchantApp.Report merchantReport = new MerchantApp.Report();
    private ManagerApp.Payment expectedPayment;
    private Merchant merchant;
    private Customer customer;

    @Given("a successful payment")
    public void aSuccessfulPayment() throws Exception {
        merchant = new Merchant();
        merchant.setCprNumber("merchant1-17");
        merchant.setFirstName("firstName1");
        merchant.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(1000);

        String merchantAccountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant), balance);
        merchant.setAccountId(merchantAccountId);

        merchant.setDtuPayId(merchantDtuPay.registerMerchant(merchant).getDtuPayId());

        customer = new Customer();
        customer.setCprNumber("customer1-17");
        customer.setFirstName("firstName1");
        customer.setLastName("lastName1");

        String customerAccountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer), balance);
        customer.setAccountId(customerAccountId);

        customer.setDtuPayId(customerDtuPay.registerCustomer(customer).getDtuPayId());

        List<CustomerApp.Token> tokens = customerDtuPay.requestTokens(customer, 5);
        customer.setTokens(tokens);

        CustomerApp.Token tokenReceivedFromCustomer = customer.provideToken();

        MerchantApp.Token token = MapperUtility.mapCustomerTokenToMerchantToken(tokenReceivedFromCustomer);
        String paymentResponse = merchantDtuPay.requestPayment(100, token, merchant.getDtuPayId());

        expectedPayment = new ManagerApp.Payment();
        expectedPayment.setAmount(100);
        expectedPayment.setCustomerToken(MapperUtility.mapCustomerTokenToManagerToken(tokenReceivedFromCustomer));
        expectedPayment.setCustomerDtuPayId(customer.getDtuPayId());
        expectedPayment.setMerchantDtuPayId(merchant.getDtuPayId());

        assertEquals("Success", paymentResponse);
    }

    @When("the manager requests a report")
    public void theManagerRequestsAReport() {
        managerReport.setPayments(managerDtuPay.requestReport().getPayments());
    }

    @Then("the manager report includes the payment")
    public void theManagerReportIncludesThePayment() {
        assertTrue(managerReport.getPayments().contains(expectedPayment));
    }

    @And("the payment includes the customer")
    public void thePaymentIncludesTheCustomer() {
        assertEquals(expectedPayment.getCustomerDtuPayId(), customer.getDtuPayId());
    }

    @When("the customer requests a report")
    public void theCustomerRequestsAReport() {
        customerReport.setPayments(customerDtuPay.requestReport(customer.getDtuPayId()).getPayments());
    }

    @When("the merchant requests a report")
    public void theMerchantRequestsAReport() {
        merchantReport.setPayments(merchantDtuPay.requestReport(merchant.getDtuPayId()).getPayments());
    }

    @Then("the customer report includes the payment")
    public void theCustomerReportIncludesThePayment() {
        assertTrue(customerReport.getPayments().contains(MapperUtility.mapManagerPaymentToCustomerPayment(expectedPayment)));
    }

    @And("the payment includes the merchant")
    public void thePaymentIncludesTheMerchant() {
        assertEquals(expectedPayment.getMerchantDtuPayId(), merchant.getDtuPayId());
    }

    @Then("the merchant report includes the payment")
    public void theMerchantReportIncludesThePayment() {
        assertTrue(merchantReport.getPayments().contains(MapperUtility.mapManagerPaymentToMerchantPayment(expectedPayment)));
    }

    @After
    public void cleanBankAccounts() {
        Customer[] customersToRetire = new Customer[]{ customer };
        Merchant[] merchantsToRetire = new Merchant[]{ merchant };

        for (Customer customerToRetire : customersToRetire) {
            try {
                bank.retireAccount(customerToRetire.getAccountId());
            } catch (Exception ignored) {
            }
        }

        for (Merchant merchantToRetire : merchantsToRetire) {
            try {
                bank.retireAccount(merchantToRetire.getAccountId());
            } catch (Exception ignored) {
            }
        }
    }
}
