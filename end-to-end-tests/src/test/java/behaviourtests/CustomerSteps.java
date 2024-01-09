package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import CustomerApp.CustomerToUserMapper;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;

public class CustomerSteps {
    private Customer customer;
    private final BankService bank = new BankServiceService().getBankServicePort();

    private final CustomerDTUPay customerDTUPay = new CustomerDTUPay();
    private Customer customerRegistrationResponse;


    @Given("customer registered in bank")
    public void customerRegisteredInBank() throws BankServiceException_Exception {
        customer = new Customer();
        customer.setCprNumber("1234567890-17");
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        BigDecimal balance = new BigDecimal(1000);

        String accountId = bank.createAccountWithBalance(CustomerToUserMapper.costumerToUser(customer), balance);
        customer.setAccountId(accountId);
    }

    @When("the customer registers with DTUPay")
    public void theCustomerRegistersWithDTUPay() {
        customerRegistrationResponse = customerDTUPay.registerCustomer(customer);
    }

    @Then("the customer is successfully registered")
    public void theCustomerIsSuccessfullyRegistered() {
        assertNotNull(customerRegistrationResponse.getDtuPayId());
    }

    @After
    public void cleanTestUser() {
        try {
            bank.retireAccount(customer.getAccountId());
        } catch (Exception ignored) {

        }

    }
}
