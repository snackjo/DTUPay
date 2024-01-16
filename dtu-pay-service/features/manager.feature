Feature: DTUPay service manager feature
    Scenario: Get report for successful payment
        When a manager requests a report
        Then a "ManagerReportRequested" event is published
        When a ManagerReportGenerated event is received
        Then report is returned