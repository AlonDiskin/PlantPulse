Feature: User journey scenario to measure the light level for a plant in his garden

  Scenario: Plant light measured
    Given user has plants in his garden
    When he open app from device home
    And open garden screen
    And select to view first listed plant detail
    When he select to use plant light meter
    Then app should open light meter screen for his viewed plant
    And display the current measured light level for plant