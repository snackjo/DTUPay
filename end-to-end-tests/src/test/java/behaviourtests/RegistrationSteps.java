package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import MerchantApp.MerchantDtuPay;
import Utility.MapperUtility;
import MerchantApp.Merchant;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class RegistrationSteps {
    private Customer customer1;
    private Customer customer2;
    private Merchant merchant1;
    private Merchant merchant2;
    private final BankService bank = new BankServiceService().getBankServicePort();

    private final MerchantDtuPay merchantDtuPay = new MerchantDtuPay();
    private final CustomerDTUPay customerDtuPay = new CustomerDTUPay();
    private Customer customerRegistrationResponse;
    private Merchant merchantRegistrationResponse;
    private final CompletableFuture<Customer> customerRegistrationResult1 = new CompletableFuture<>();
    private final CompletableFuture<Customer> customerRegistrationResult2 = new CompletableFuture<>();


    @Given("customer registered in bank")
    public void customerRegisteredInBank() throws BankServiceException_Exception {
        customer1 = new Customer();
        customer1.setCprNumber("1234567890-17");
        customer1.setFirstName("firstName");
        customer1.setLastName("lastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer1), balance);
        customer1.setAccountId(accountId);
    }

    @When("the customer registers with DTUPay")
    public void theCustomerRegistersWithDTUPay() {
        customerRegistrationResponse = customerDtuPay.registerCustomer(customer1);
    }

    @Then("the customer is successfully registered")
    public void theCustomerIsSuccessfullyRegistered() {
        assertNotNull(customerRegistrationResponse.getDtuPayId());
    }

    @After
    public void cleanTestUser() {
        try {
            bank.retireAccount(customer1.getAccountId());
            bank.retireAccount(customer2.getAccountId());
            bank.retireAccount(merchant1.getAccountId());
            bank.retireAccount(merchant2.getAccountId());
        } catch (Exception ignored) {

        }
    }

    @And("another customer registered in bank")
    public void anotherCustomerRegisteredInBank() throws BankServiceException_Exception {
        customer2 = new Customer();
        customer2.setCprNumber("9876543210-17");
        customer2.setFirstName("firstName2");
        customer2.setLastName("lastName2");
        BigDecimal balance = new BigDecimal(2000);

        String accountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer2), balance);
        customer2.setAccountId(accountId);
    }

    @When("the two customers are registered with DTUPay at the same time")
    public void theTwoCustomersAreRegisteredWithDTUPayAtTheSameTime() {
        Thread thread1 = new Thread(() -> customerRegistrationResult1.complete(customerDtuPay.registerCustomer(customer1)));
        Thread thread2 = new Thread(() -> customerRegistrationResult2.complete(customerDtuPay.registerCustomer(customer2)));
        thread1.start();
        thread2.start();
    }

    @Then("the first customer has a non-empty DTUPay ID")
    public void theFirstCustomerHasANonEmptyDTUPayID() {
        assertNotNull(customerRegistrationResult1.join().getDtuPayId());
    }

    @And("the second customer has a non-empty DTUPay ID different from the first customer")
    public void theSecondCustomerHasANonEmptyDTUPayIDDifferentFromTheFirstCustomer() throws ExecutionException, InterruptedException {
        assertNotNull(customerRegistrationResult2.join().getDtuPayId());
        String customerDtuPayId1 = customerRegistrationResult1.get().getDtuPayId();
        String customerDtuPayId2 = customerRegistrationResult2.get().getDtuPayId();
        assertNotEquals(customerDtuPayId1, customerDtuPayId2);
    }

    @Given("merchant registered in bank")
    public void merchantRegisteredInBank() throws BankServiceException_Exception {
        merchant1 = new Merchant();
        merchant1.setCprNumber("1234567890-17");
        merchant1.setFirstName("firstName");
        merchant1.setLastName("lastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant1), balance);
        merchant1.setAccountId(accountId);
    }

    @When("the merchant registers with DTUPay")
    public void theMerchantRegistersWithDTUPay() {
        merchantRegistrationResponse = merchantDtuPay.registerMerchant(merchant1);
    }

    @Then("the merchant is successfully registered")
    public void theMerchantIsSuccessfullyRegistered() {
        assertNotNull(merchantRegistrationResponse.getDtuPayId());
    }
}
