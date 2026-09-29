Feature: User journey scenario to view all plants in user garden, that are from the herbs category

  Scenario: Garden herbs listed
    Given user open app from device home
    And he has plants in his garden
    When he open garden screen
    Then all his garden plants should be listed
    When he select to filter only plants from the herbs category
    Then app should list only herbs frob garden plants
    When user select to view first listed herb
    Then app should open plant detail screen
    And show plant data