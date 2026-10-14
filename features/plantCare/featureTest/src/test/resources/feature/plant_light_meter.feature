Feature: User garden plants light measuring

  # Rule: Provide plant light measurement

  @light-measured
  Scenario: Plant light measured
    Given user has a plant in garden
    When he open plant care options screen
    And select to use plant light meter
    Then app should open light meter screen
    And display current light measure, with plant recommended scale
    And light meter should conclude if light amount is sufficient for viewed plant

  # Rule: enable light data sharing

  @data-shared
  Scenario: Light measurement shared
    Given user has a plant in garden
    When he open plant light meter screen
    And select to share the measured light data
    Then app should open device sharing menu

  # Rule: provide instructions for light meter usage

  @instructions-provided
  Scenario: Light meter instructions provided
    Given user has a plant in garden
    When he open plant light meter screen
    Then app should show user instructions for light meter usage
