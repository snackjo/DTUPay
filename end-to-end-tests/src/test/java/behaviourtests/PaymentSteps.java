package behaviourtests;

import CustomerApp.Customer;
import CustomerApp.CustomerDTUPay;
import MerchantApp.Merchant;
import MerchantApp.MerchantDtuPay;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaymentSteps {
    StateHolder stateHolder;
    Customer customer;
    Merchant merchant;
    private final MerchantDtuPay merchantDtuPay = new MerchantDtuPay();
    private final CustomerDTUPay customerDtuPay = new CustomerDTUPay();

    public PaymentSteps(StateHolder stateHolder) {
        this.stateHolder = stateHolder;
    }
    @And("the merchant is registered with DTUPay")
    public void theMerchantIsRegisteredWithDTUPay() {
        merchant = stateHolder.getMerchant();
        merchantDtuPay.registerMerchant(merchant);
    }

    @And("the customer is registered with DTUPay")
    public void theCustomerIsRegisteredWithDTUPay() throws Exception {
        customer = stateHolder.getCustomer();
        customerDtuPay.registerCustomer(customer);
    }

    @And("the customer has generated tokens")
    public void theCustomerHasGeneratedTokens() {
        throw new PendingException();
    }

    @Given("the merchant has received a token from the customer")
    public void theMerchantHasReceivedATokenFromTheCustomer() {
        throw new PendingException();
    }

    @When("the merchant initiates a payment of {int} using the token")
    public void theMerchantInitiatesAPaymentOfUsingTheToken(int arg0) {
        throw new PendingException();
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
