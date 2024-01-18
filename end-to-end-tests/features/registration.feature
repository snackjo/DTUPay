@author=Emil
Feature: Registration feature

    Scenario: Successful customer registration
        Given customer registered in the bank
        When the customer registers with DTUPay
        Then the customer is successfully registered

    Scenario: Customer registration race condition
        Given customer registered in the bank
        And another customer registered in bank
        When the two customers are registered with DTUPay at the same time
        Then the first customer has a non-empty DTUPay ID
        And the second customer has a non-empty DTUPay ID different from the first customer

    Scenario: Successful merchant registration
        Given merchant registered in the bank
        When the merchant registers with DTUPay
        Then the merchant is successfully registered

    Scenario: Merchant registration race condition
        Given merchant registered in the bank
        And another merchant registered in bank
        When the two merchants are registered with DTUPay at the same time
        Then the first merchant has a non-empty DTUPay ID
        And the second merchant has a non-empty DTUPay ID different from the first customer

    Scenario: Successful merchant deregistration
        Given merchant registered in the bank
        And the merchant is registered with DTUPay
        When the merchant deregisters from DTUPay
        Then the merchant is successfully deregistered

    Scenario: Merchant deregistration race condition
        Given merchant registered in the bank
        And the merchant is registered with DTUPay
        Given another merchant registered in bank
        And the other merchant is registered in with DTUPay
        When the two merchants are deregistered with DTUPay at the same time
        Then the first merchant is successfully deregistered
        And the second merchant is also successfully deregistered

    Scenario: Successful customer deregistration
        Given customer registered in the bank
        And the customer is registered with DTUPay
        When the customer deregisters from DTUPay
        Then the customer is successfully deregistered

    Scenario: Customer deregistration race condition
        Given customer registered in the bank
        And the customer is registered with DTUPay
        Given another customer registered in bank
        And the other customer is registered in with DTUPay
        When the two customers are deregistered with DTUPay at the same time
        Then the first customer is successfully deregistered
        And the second customer is also successfully deregistered