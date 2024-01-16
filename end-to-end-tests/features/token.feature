Feature: Token feature

    Scenario: Successful token generation
        Given customer registered in DTUPay with 0 tokens
        When the customer requests 2 tokens
        Then the customer receives 2 tokens

    Scenario: Successful token generation for two customers (race condition)
        Given customer registered in DTUPay with 0 tokens
        Given another customer registered in DTUPay with 0 tokens
        When both customers request 2 tokens at the same time
        Then the first customer receives 2 tokens
        And the second customer receives 2 tokens
        And the tokens they receive are different

    Scenario Outline: Customer requests too many tokens
        Given customer registered in DTUPay with 0 tokens
        When the customer requests <tokenAmount> tokens
        Then the request is rejected

        Examples:
            | tokenAmount |
            | -1          |
            | 0           |
            | 6           |