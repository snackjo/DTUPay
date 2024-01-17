Feature: Payment feature
    Scenario: Customer pays merchant successfully
        Given merchant registered in bank with a balance of 1000
        And the merchant is also registered with DTUPay
        Given customer registered in bank with a balance of 1000
        And the customer is also registered with DTUPay
        And the customer has generated tokens
        Given the merchant has received a token from the customer
        When the merchant requests a payment of 100 using the token
        Then the payment is successful
        And the merchant's balance is 1100
        And the customer's balance is 900


