Feature: Customer registration feature

    Scenario: Successful customer registration
        Given customer registered in bank
        When the customer registers with DTUPay
        Then the customer is successfully registered

    Scenario: Customer registration race condition
        Given customer registered in bank
        And another customer registered in bank
        When the two customers are registered with DTUPay at the same time
        Then the first customer has a non-empty DTUPay ID
        And the second customer has a non-empty DTUPay ID different from the first customer