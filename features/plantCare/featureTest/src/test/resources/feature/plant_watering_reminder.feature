Feature: User garden plants water reminder

  # Rule: show plant recommended watering

  @recommended-watering
  Scenario: Watering recommendation shown
    Given user has plant in his garden
    When he open water reminder scheduler screen for plant
    Then app should show the recommended watering for plant
