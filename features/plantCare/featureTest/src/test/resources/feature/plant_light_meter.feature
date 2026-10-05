Feature: User garden plants light measuring

  # Rule: Provide plant light measurement

  @light-measured
  Scenario: Plant light measured
    Given user has a plant in garden
    When he open plant care options screen
    And select to use plant light meter
    Then app should open light meter screen
    And display current light measure, with plant recommended scale

  @sufficiency-determined
  Scenario: Light sufficiency determined
    Given user has a plant in garden
    When he open plant garden detail screen
    And select to use plant light meter
    Then app should open light meter screen
    And light meter should conclude if light amount is sufficient for viewed plant
