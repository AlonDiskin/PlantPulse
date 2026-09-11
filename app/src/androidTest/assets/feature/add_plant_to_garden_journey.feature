Feature: User journey scenario to find and add new plant to user garden

  Scenario: Plant added
    Given user open app from device home
    When he select to add new plant to his garden
    Then plants search screen should open
    When he enters a query for monstera plant species
    Then app should list all known monstera species plants
    When user select the monstera plant he wants to add
    Then app should confirm plant added
    When user navigates to garden screen
    Then app should list his new added monstera