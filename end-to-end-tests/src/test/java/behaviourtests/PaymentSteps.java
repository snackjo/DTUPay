package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import CustomerApp.Token;
import MerchantApp.Merchant;
import MerchantApp.MerchantDtuPay;
import Utility.MapperUtility;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

public class PaymentSteps {
    StateHolder stateHolder;
    Customer customer;
    Merchant merchant;
    Token tokenReceivedFromCustomer;
    private final MerchantDtuPay merchantDtuPay = new MerchantDtuPay();
    private final CustomerDTUPay customerDtuPay = new CustomerDTUPay();

    public PaymentSteps(StateHolder stateHolder) {
        this.stateHolder = stateHolder;
    }
    @And("the merchant is registered with DTUPay")
    public void theMerchantIsRegisteredWithDTUPay() {
        merchant = stateHolder.getMerchant();
        merchant.setDtuPayId(merchantDtuPay.registerMerchant(merchant).getDtuPayId());
    }

    @And("the customer is registered with DTUPay")
    public void theCustomerIsRegisteredWithDTUPay() throws Exception {
        customer = stateHolder.getCustomer();
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

    @When("the merchant initiates a payment of {int} using the token")
    public void theMerchantInitiatesAPaymentOfUsingTheToken(int paymentAmount) throws Exception {
        MerchantApp.Token token = MapperUtility.mapToken(tokenReceivedFromCustomer);
        merchantDtuPay.initiatePayment(paymentAmount, token, merchant.getDtuPayId());
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        throw new PendingException();
    }

    @And("the merchant's balance is {int}")
    public void theMerchantSBalanceIs(int arg0) {
        throw new PendingException();
    }

    @And("the customer's balance is {int}")
    public void theCustomerSBalanceIs(int arg0) {
        throw new PendingException();
    }
}
