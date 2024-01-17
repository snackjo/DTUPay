package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDtuPay;
import CustomerApp.Token;
import MerchantApp.Merchant;
import MerchantApp.MerchantDtuPay;
import Utility.MapperUtility;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PaymentSteps {
    Customer customer;
    Merchant merchant;
    Token tokenReceivedFromCustomer;
    private final BankService bank = new BankServiceService().getBankServicePort();
    private final MerchantDtuPay merchantDtuPay = new MerchantDtuPay();
    private final CustomerDtuPay customerDtuPay = new CustomerDtuPay();
    private String paymentResponse;

    @Given("merchant registered in bank with a balance of {int}")
    public void merchantRegisteredInBankWithABalanceOf(int startingBalance) throws BankServiceException_Exception {
        merchant = new Merchant();
        merchant.setCprNumber("merchant1-17");
        merchant.setFirstName("firstName1");
        merchant.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(startingBalance);

        String accountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant), balance);
        merchant.setAccountId(accountId);
    }

    @And("the merchant is also registered with DTUPay")
    public void theMerchantIsAlsoRegisteredWithDTUPay() {
        merchant.setDtuPayId(merchantDtuPay.registerMerchant(merchant).getDtuPayId());
    }

    @Given("customer registered in bank with a balance of {int}")
    public void customerRegisteredInBankWithABalanceOf(int startingBalance) throws BankServiceException_Exception {
        customer = new Customer();
        customer.setCprNumber("customer1-17");
        customer.setFirstName("firstName1");
        customer.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(startingBalance);

        String accountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer), balance);
        customer.setAccountId(accountId);
    }

    @And("the customer is also registered with DTUPay")
    public void theCustomerIsAlsoRegisteredWithDTUPay() {
        customer.setDtuPayId(customerDtuPay.registerCustomer(customer).getDtuPayId());
    }

    @And("the customer has generated tokens")
    public void theCustomerHasGeneratedTokens() throws Exception {
        List<Token> tokens = customerDtuPay.requestTokens(customer, 5);
        customer.setTokens(tokens);
    }

    @Given("the merchant has received a token from the customer")
    public void theMerchantHasReceivedATokenFromTheCustomer() throws Exception {
        tokenReceivedFromCustomer = customer.provideToken();
    }

    @When("the merchant requests a payment of {int} using the token")
    public void theMerchantRequestsAPaymentOfUsingTheToken(int paymentAmount) throws Exception {
        MerchantApp.Token token = MapperUtility.mapCustomerTokenToMerchantToken(tokenReceivedFromCustomer);
        paymentResponse = merchantDtuPay.requestPayment(paymentAmount, token, merchant.getDtuPayId());
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals("Success", paymentResponse);
    }

    @And("the merchant's balance is {int}")
    public void theMerchantSBalanceIs(int expectedBalance) throws BankServiceException_Exception {
        Account account = bank.getAccount(merchant.getAccountId());
        assertEquals(BigDecimal.valueOf(expectedBalance), account.getBalance());
    }

    @And("the customer's balance is {int}")
    public void theCustomerSBalanceIs(int expectedBalance) throws BankServiceException_Exception {
        Account account = bank.getAccount(customer.getAccountId());
        assertEquals(BigDecimal.valueOf(expectedBalance), account.getBalance());
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
