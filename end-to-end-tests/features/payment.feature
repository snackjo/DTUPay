@author=Peter
Feature: Payment feature
    Scenario: Customer pays merchant successfully
        Given the merchant "Alice" registered in bank with a balance of 1000
        And the merchant "Alice" is also registered with DTUPay
        Given the customer "Bob" registered in bank with a balance of 1000
        And the customer "Bob" is also registered with DTUPay
        And "Bob" has generated tokens
        Given "Alice" has received a token from "Bob"
        When "Alice" requests a payment of 100
        Then the payment is successful
        And the merchant "Alice" balance is 1100
        And the customer "Bob" balance is 900

    Scenario: Two different payments at the same time
        Given the merchant "Alice" registered in bank with a balance of 1000
        And the merchant "Alice" is also registered with DTUPay
        Given the customer "Bob" registered in bank with a balance of 1000
        And the customer "Bob" is also registered with DTUPay
        And "Bob" has generated tokens
        Given the merchant "Charlie" registered in bank with a balance of 1000
        And the merchant "Charlie" is also registered with DTUPay
        Given the customer "Dave" registered in bank with a balance of 1000
        And the customer "Dave" is also registered with DTUPay
        And "Dave" has generated tokens
        Given "Alice" has received a token from "Bob"
        And "Charlie" has received a token from "Dave"
        When "Alice" wants to request a payment of 100
        And "Charlie" wants to request a payment of 500
        And the two payments are requested at the same time
        Then the two payments are successful
        And the merchant "Alice" balance is 1100
        And the customer "Bob" balance is 900
        And the merchant "Charlie" balance is 1500
        And the customer "Dave" balance is 500
