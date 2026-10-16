Feature: User journey scenario to schedule a watering reminder for one of his garden plants

  Scenario: Water reminder scheduled
    Given user has plants in his garden
    When he open app from device home
    And open garden screen
    And select to view first listed plant detail
    When he select to schedule water reminder for plant
    Then app should open water reminder screen
    When he set reminder as weekly at friday 14:00,with no sound, and message of 'Water Snake Plant'
    Then app should schedule his reminder