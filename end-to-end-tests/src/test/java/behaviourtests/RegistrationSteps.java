package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import MerchantApp.Merchant;
import MerchantApp.MerchantDtuPay;
import Utility.MapperUtility;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class RegistrationSteps {
    private Customer customer1;
    private final StateHolder stateHolder;
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
    private final CompletableFuture<Merchant> merchantRegistrationResult1 = new CompletableFuture<>();
    private final CompletableFuture<Merchant> merchantRegistrationResult2 = new CompletableFuture<>();
    private Response merchantDeregistrationResponse;
    private final CompletableFuture<Response> merchantDeregistrationResult1 = new CompletableFuture<>();
    private final CompletableFuture<Response> merchantDeregistrationResult2 = new CompletableFuture<>();
    private Response customerDeregistrationResponse;
    private final CompletableFuture<Response> customerDeregistrationResult1 = new CompletableFuture<>();
    private final CompletableFuture<Response> customerDeregistrationResult2 = new CompletableFuture<>();

    public RegistrationSteps(StateHolder stateHolder) {
        customer1 = stateHolder.getCustomer();
        this.stateHolder = stateHolder;
    }

    @Given("customer registered in bank with a balance of {int}")
    public void customerRegisteredInBankWithABalanceOf(int startingBalance) throws BankServiceException_Exception {
        customer1 = new Customer();
        customer1.setCprNumber("customer1-17");
        customer1.setFirstName("firstName1");
        customer1.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(startingBalance);

        String accountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer1), balance);
        customer1.setAccountId(accountId);
        stateHolder.setCustomer(customer1);
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
    public void cleanBankAccounts() {
        Customer[] customersToRetire = new Customer[]{customer1, customer2};
        Merchant[] merchantsToRetire = new Merchant[]{merchant1, merchant2};

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

    @And("another customer registered in bank")
    public void anotherCustomerRegisteredInBank() throws BankServiceException_Exception {
        customer2 = new Customer();
        customer2.setCprNumber("customer2-17");
        customer2.setFirstName("firstName2");
        customer2.setLastName("lastName2");
        BigDecimal balance = new BigDecimal(2000);

        String accountId = bank.createAccountWithBalance(MapperUtility.costumerToUser(customer2), balance);
        customer2.setAccountId(accountId);
    }

    @When("the two customers are registered with DTUPay at the same time")
    public void theTwoCustomersAreRegisteredWithDTUPayAtTheSameTime() {
        Thread thread1 = createCustomerRegistrationThread(customerRegistrationResult1, customer1);
        Thread thread2 = createCustomerRegistrationThread(customerRegistrationResult2, customer2);
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

    @Given("merchant registered in bank with a balance of {int}")
    public void merchantRegisteredInBankWithABalanceOf(int startingBalance) throws BankServiceException_Exception {
        merchant1 = new Merchant();
        merchant1.setCprNumber("merchant1-17");
        merchant1.setFirstName("firstName1");
        merchant1.setLastName("lastName1");
        BigDecimal balance = new BigDecimal(startingBalance);

        String accountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant1), balance);
        merchant1.setAccountId(accountId);
        stateHolder.setMerchant(merchant1);
    }

    @When("the merchant registers with DTUPay")
    public void theMerchantRegistersWithDTUPay() {
        merchantRegistrationResponse = merchantDtuPay.registerMerchant(merchant1);
    }

    @Then("the merchant is successfully registered")
    public void theMerchantIsSuccessfullyRegistered() {
        assertNotNull(merchantRegistrationResponse.getDtuPayId());
    }

    @And("another merchant registered in bank")
    public void anotherMerchantRegisteredInBank() throws BankServiceException_Exception {
        merchant2 = new Merchant();
        merchant2.setCprNumber("merchant2-17");
        merchant2.setFirstName("firstName2");
        merchant2.setLastName("lastName2");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = bank.createAccountWithBalance(MapperUtility.merchantToUser(merchant2), balance);
        merchant2.setAccountId(accountId);
    }

    @When("the two merchants are registered with DTUPay at the same time")
    public void theTwoMerchantsAreRegisteredWithDTUPayAtTheSameTime() {
        Thread thread1 = new Thread(() -> merchantRegistrationResult1.complete(merchantDtuPay.registerMerchant(merchant1)));
        Thread thread2 = new Thread(() -> merchantRegistrationResult2.complete(merchantDtuPay.registerMerchant(merchant2)));
        thread1.start();
        thread2.start();
    }

    @Then("the first merchant has a non-empty DTUPay ID")
    public void theFirstMerchantHasANonEmptyDTUPayID() {
        assertNotNull(merchantRegistrationResult1.join().getDtuPayId());
    }

    @And("the second merchant has a non-empty DTUPay ID different from the first customer")
    public void theSecondMerchantHasANonEmptyDTUPayIDDifferentFromTheFirstCustomer() throws ExecutionException, InterruptedException {
        assertNotNull(merchantRegistrationResult2.join().getDtuPayId());
        String merchantDtuPayId1 = merchantRegistrationResult1.get().getDtuPayId();
        String merchantDtuPayId2 = merchantRegistrationResult2.get().getDtuPayId();
        assertNotEquals(merchantDtuPayId1, merchantDtuPayId2);
    }

    @When("the merchant deregisters from DTUPay")
    public void theMerchantDeregistersFromDTUPay() {
        merchantDeregistrationResponse = merchantDtuPay.deregisterMerchant(stateHolder.getMerchant().getDtuPayId());
    }

    @Then("the merchant is successfully deregistered")
    public void theMerchantIsSuccessfullyDeregistered() {
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), merchantDeregistrationResponse.getStatus());
    }

    @And("the other merchant is registered in with DTUPay")
    public void theOtherMerchantIsRegisteredInWithDTUPay() {
        merchant2.setDtuPayId(merchantDtuPay.registerMerchant(merchant2).getDtuPayId());
    }

    @When("the two merchants are deregistered with DTUPay at the same time")
    public void theTwoMerchantsAreDeregisteredWithDTUPayAtTheSameTime() {
        Thread thread1 = createMerchantDeregistaionThread(merchantDeregistrationResult1, merchant1.getDtuPayId());
        Thread thread2 = createMerchantDeregistaionThread(merchantDeregistrationResult2, merchant2.getDtuPayId());
        thread1.start();
        thread2.start();
    }

    @Then("the first merchant is successfully deregistered")
    public void theFirstMerchantIsSuccessfullyDeregistered() {
        int responseStatus = merchantDeregistrationResult1.join().getStatus();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), responseStatus);
    }

    @And("the second merchant is also successfully deregistered")
    public void theSecondMerchantIsAlsoSuccessfullyDeregistered() {
        int responseStatus = merchantDeregistrationResult2.join().getStatus();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), responseStatus);
    }

    @When("the customer deregisters from DTUPay")
    public void theCustomerDeregistersFromDTUPay() {
        customerDeregistrationResponse = customerDtuPay.deregisterCustomer(stateHolder.getCustomer().getDtuPayId());
    }

    @Then("the customer is successfully deregistered")
    public void theCustomerIsSuccessfullyDeregistered() {
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), customerDeregistrationResponse.getStatus());
    }

    @And("the other customer is registered in with DTUPay")
    public void theOtherCustomerIsRegisteredInWithDTUPay() {
        customer2.setDtuPayId(customerDtuPay.registerCustomer(customer2).getDtuPayId());
    }

    @When("the two customers are deregistered with DTUPay at the same time")
    public void theTwoCustomersAreDeregisteredWithDTUPayAtTheSameTime() {
        Thread thread1 = createCustomerDeregistrationThread(customerDeregistrationResult1, customer1.getDtuPayId());
        Thread thread2 = createCustomerDeregistrationThread(customerDeregistrationResult2, customer2.getDtuPayId());
        thread1.start();
        thread2.start();
    }

    @Then("the first customer is successfully deregistered")
    public void theFirstCustomerIsSuccessfullyDeregistered() {
        int responseStatus = customerDeregistrationResult1.join().getStatus();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), responseStatus);
    }

    @And("the second customer is also successfully deregistered")
    public void theSecondCustomerIsAlsoSuccessfullyDeregistered() {
        int responseStatus = customerDeregistrationResult1.join().getStatus();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), responseStatus);
    }

    private Thread createMerchantDeregistaionThread(CompletableFuture<Response> completableFuture, String dtuPayId) {
        return new Thread(() -> {
            try {
                completableFuture.complete(merchantDtuPay.deregisterMerchant(dtuPayId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Thread createCustomerRegistrationThread(CompletableFuture<Customer> completableFuture, Customer customer) {
        return new Thread(() -> {
            try {
                completableFuture.complete(customerDtuPay.registerCustomer(customer));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Thread createCustomerDeregistrationThread(CompletableFuture<Response> completableFuture, String dtuPayId) {
        return new Thread(() -> {
            try {
                completableFuture.complete(customerDtuPay.deregisterCustomer(dtuPayId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
