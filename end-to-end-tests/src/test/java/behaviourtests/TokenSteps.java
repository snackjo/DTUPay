package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import CustomerApp.Token;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenSteps {

    private Customer customer;
    private final CustomerDTUPay customerDtuPay = new CustomerDTUPay();
    private List<Token> tokensRequestResponse;

    @Given("customer registered in DTUPay with {int} tokens")
    public void customerRegisteredInDTUPayWithTokens(int tokenAmount) throws Exception {
        customer = new Customer();
        customer.setCprNumber("customer1-17");
        customer.setFirstName("firstName1");
        customer.setLastName("lastName1");
        customer.setAccountId("fakeAccountId-17");

        String dtuPayId = customerDtuPay.registerCustomer(customer).getDtuPayId();

        customer.setDtuPayId(dtuPayId);

        assertEquals(tokenAmount, customer.getTokens().size());
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int tokenAmount) throws Exception {
        tokensRequestResponse = customerDtuPay.requestTokens(customer, tokenAmount);
    }

    @Then("the customer receives {int} tokens")
    public void theCustomerReceivesTokens(int tokenAmount) {
        assertEquals(tokenAmount, tokensRequestResponse.size());
    }
}
