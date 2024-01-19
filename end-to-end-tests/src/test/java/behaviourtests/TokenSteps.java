package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDtuPay;
import CustomerApp.DtuPayException;
import CustomerApp.Token;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

// @author Emil
public class TokenSteps {

    private Customer customer1;
    private Customer customer2;
    private final CustomerDtuPay customerDtuPay = new CustomerDtuPay();
    private List<Token> tokensRequestResponse;
    private final CompletableFuture<List<Token>> tokenRequestResult1 = new CompletableFuture<>();
    private final CompletableFuture<List<Token>> tokenRequestResult2 = new CompletableFuture<>();
    private DtuPayException tokenRequestException;

    @Given("customer registered in DTUPay with {int} tokens")
    public void customerRegisteredInDTUPayWithTokens(int tokenAmount) {
        customer1 = new Customer();
        customer1.setCprNumber("customer1-17");
        customer1.setFirstName("firstName1");
        customer1.setLastName("lastName1");
        customer1.setAccountId("fakeAccountId-17");

        String dtuPayId = customerDtuPay.registerCustomer(customer1).getDtuPayId();

        customer1.setDtuPayId(dtuPayId);

        assertEquals(tokenAmount, customer1.getTokens().size());
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int tokenAmount) {
        try {
            tokensRequestResponse = customerDtuPay.requestTokens(customer1, tokenAmount);
        } catch (DtuPayException e) {
            tokenRequestException = e;
        }
    }

    @Then("the customer receives {int} tokens")
    public void theCustomerReceivesTokens(int tokenAmount) {
        assertEquals(tokenAmount, tokensRequestResponse.size());
    }

    @Given("another customer registered in DTUPay with {int} tokens")
    public void anotherCustomerRegisteredInDTUPayWithTokens(int tokenAmount) {
        customer2 = new Customer();
        customer2.setCprNumber("customer1-17");
        customer2.setFirstName("firstName1");
        customer2.setLastName("lastName1");
        customer2.setAccountId("fakeAccountId-17");

        String dtuPayId = customerDtuPay.registerCustomer(customer2).getDtuPayId();

        customer2.setDtuPayId(dtuPayId);

        assertEquals(tokenAmount, customer2.getTokens().size());
    }

    @When("both customers request {int} tokens at the same time")
    public void bothCustomersRequestTokens(int tokenAmount) {
        Thread thread1 = createTokenRequestThread(tokenRequestResult1, customer1, tokenAmount);
        Thread thread2 = createTokenRequestThread(tokenRequestResult2, customer2, tokenAmount);
        thread1.start();
        thread2.start();
    }

    @Then("the first customer receives {int} tokens")
    public void theFirstCustomerReceivesTokens(int tokenAmount) {
        List<Token> tokens = tokenRequestResult1.join();
        customer1.setTokens(tokens);
        assertEquals(tokenAmount, tokens.size());
    }

    @And("the second customer receives {int} tokens")
    public void theSecondCustomerReceivesTokens(int tokenAmount) {
        List<Token> tokens = tokenRequestResult2.join();
        customer2.setTokens(tokens);
        assertEquals(tokenAmount, tokens.size());
    }

    @And("the tokens they receive are different")
    public void theTokensTheyReceiveAreDifferent() {
        for (Token customer1Token : customer1.getTokens()) {
            for (Token customer2Token : customer2.getTokens()) {
                assertNotEquals(customer1Token.getId(), customer2Token.getId());
            }
        }
    }

    @Then("the request is rejected")
    public void theRequestIsRejected() {
        assertNotNull(tokenRequestException);
    }

    @When("the customer requests {int} tokens twice at the same time")
    public void theCustomerRequestsTokensTwiceAtTheSameTime(int tokenAmount) {
        Thread thread1 = createTokenRequestThread(tokenRequestResult1, customer1, tokenAmount);
        Thread thread2 = createTokenRequestThread(tokenRequestResult2, customer1, tokenAmount);
        thread1.start();
        thread2.start();
    }

    @Then("only one requests succeeds and the customer gets {int} tokens")
    public void onlyOneRequestsSucceedsAndTheCustomerGetsTokens(int tokenAmount) {
        List<Token> tokens1 = tokenRequestResult1.join();
        List<Token> tokens2 = tokenRequestResult2.join();

        if (tokens1 != null) {
            assertEquals(tokenAmount, tokens1.size());
        } else {
            assertEquals(tokenAmount, tokens2.size());
        }

        assertNotNull(tokenRequestException);
    }

    private Thread createTokenRequestThread(CompletableFuture<List<Token>> completableFuture, Customer customer, int tokenAmount) {
        return new Thread(() -> {
            try {
                completableFuture.complete(customerDtuPay.requestTokens(customer, tokenAmount));
            } catch (DtuPayException e) {
                tokenRequestException = e;
                completableFuture.complete(null);
            }
        });
    }

    @And("the message is {string}")
    public void theMessageIs(String expectedMessage) {
        assertEquals(expectedMessage, tokenRequestException.getMessage());
    }
}
