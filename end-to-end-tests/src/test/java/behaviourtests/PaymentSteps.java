package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDtuPay;
import CustomerApp.DtuPayException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

// @author Bastian
public class PaymentSteps {
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Merchant> merchants = new ConcurrentHashMap<>();
    private final Map<String, Token> tokenReceivedFromCustomer = new ConcurrentHashMap<>();
    private final BankService bank = new BankServiceService().getBankServicePort();
    private final MerchantDtuPay merchantDtuPay = new MerchantDtuPay();
    private final CustomerDtuPay customerDtuPay = new CustomerDtuPay();
    private final Map<String, String> paymentResponses = new ConcurrentHashMap<>();
    private final List<Thread> threads = new ArrayList<>();

    @Given("the merchant {string} registered in bank with a balance of {int}")
    public void theMerchantRegisteredInBankWithABalanceOf(String firstName, int startingBalance) throws BankServiceException_Exception {
        Merchant merchant = new Merchant();
        merchant.setCprNumber(firstName + "-17");
        merchant.setFirstName(firstName);
        merchant.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(startingBalance);

        String accountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant), balance);
        merchant.setAccountId(accountId);
        merchants.put(firstName, merchant);
    }

    @And("the merchant {string} is also registered with DTUPay")
    public void theMerchantIsAlsoRegisteredWithDTUPay(String firstName) {
        merchants.get(firstName).setDtuPayId(merchantDtuPay.registerMerchant(merchants.get(firstName)).getDtuPayId());
    }

    @Given("the customer {string} registered in bank with a balance of {int}")
    public void theCustomerRegisteredInBankWithABalanceOf(String firstName, int startingBalance) throws BankServiceException_Exception {
        Customer customer = new Customer();
        customer.setCprNumber(firstName + "-17");
        customer.setFirstName(firstName);
        customer.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(startingBalance);

        String accountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer), balance);
        customer.setAccountId(accountId);
        customers.put(firstName, customer);
    }

    @And("the customer {string} is also registered with DTUPay")
    public void theCustomerIsAlsoRegisteredWithDTUPay(String firstName) {
        customers.get(firstName).setDtuPayId(customerDtuPay.registerCustomer(customers.get(firstName)).getDtuPayId());
    }

    @And("{string} has generated tokens")
    public void hasGeneratedTokens(String firstName) throws DtuPayException {
        List<Token> tokens = customerDtuPay.requestTokens(customers.get(firstName), 5);
        customers.get(firstName).setTokens(tokens);
    }

    @Given("{string} has received a token from {string}")
    public void hasReceivedATokenFrom(String merchantFirstName, String customerFirstName) throws Exception {
        tokenReceivedFromCustomer.put(merchantFirstName, customers.get(customerFirstName).provideToken());
    }

    @When("{string} requests a payment of {int}")
    public void requestsAPaymentOf(String merchantFirstName, int paymentAmount) throws Exception {
        MerchantApp.Token token = MapperUtility.mapCustomerTokenToMerchantToken(tokenReceivedFromCustomer.get(merchantFirstName));
        paymentResponses.put(
                merchantFirstName,
                merchantDtuPay.requestPayment(
                        paymentAmount,
                        token,
                        merchants.get(merchantFirstName).getDtuPayId()).getMessage());
    }

    @When("{string} wants to request a payment of {int}")
    public void wantsToRequestAPaymentOf(String merchantFirstName, int paymentAmount) {
        MerchantApp.Token token = MapperUtility.mapCustomerTokenToMerchantToken(tokenReceivedFromCustomer.get(merchantFirstName));
        Thread thread = new Thread(() -> {
            try {
                paymentResponses.put(
                        merchantFirstName,
                        merchantDtuPay.requestPayment(
                                paymentAmount,
                                token,
                                merchants.get(merchantFirstName).getDtuPayId()).getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        threads.add(thread);
    }

    @And("the two payments are requested at the same time")
    public void theTwoPaymentsAreRequestedAtTheSameTime() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals("Success", paymentResponses.values().toArray()[0]);
    }

    @Then("the two payments are successful")
    public void theTwoPaymentsAreSuccessful() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(2, paymentResponses.values().size());
        for (String paymentResponse : paymentResponses.values()) {
            assertEquals("Success", paymentResponse);
        }
    }

    @And("the merchant {string} balance is {int}")
    public void theMerchantBalanceIs(String firstName, int expectedBalance) throws BankServiceException_Exception {
        Account account = bank.getAccount(merchants.get(firstName).getAccountId());
        assertEquals(BigDecimal.valueOf(expectedBalance), account.getBalance());
    }

    @And("the customer {string} balance is {int}")
    public void theCustomerBalanceIs(String firstName, int expectedBalance) throws BankServiceException_Exception {
        Account account = bank.getAccount(customers.get(firstName).getAccountId());
        assertEquals(BigDecimal.valueOf(expectedBalance), account.getBalance());
    }

    @After
    public void cleanBankAccounts() {
        for (Customer customerToRetire : customers.values()) {
            try {
                bank.retireAccount(customerToRetire.getAccountId());
            } catch (Exception ignored) {
            }
        }

        for (Merchant merchantToRetire : merchants.values()) {
            try {
                bank.retireAccount(merchantToRetire.getAccountId());
            } catch (Exception ignored) {
            }
        }
    }
}
