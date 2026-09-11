## INITIAL UNIT TEST CLASS CREATION PROTOCOL (TESTER)
When first creating a unit test class, as the Tester, strictly follow this structure:
1. **Source set location**: all unit tests should be created in the local test source set.
2. **Unit test class type**: for kotlin module use junit 5. for android library/ application module, use robolectric with AndroidJUnit4 runner coupled with `@LooperMode(LooperMode.Mode.PAUSED)` annotation.
3. **Test class properties/members**: do not include the system under test, nor any dependent collaborators.
4. **Test Cases Naming**: use this example "whenExecutingSearch_WithEmptyQuery_ShouldReturnEmptyList()"
5. **Test Cases structure**: Write empty test cases for the described functionalities, add the `TODO("Implement test case")` function call inside body.
6. **Unit test class junit rules**: for classes that test ui controller(activity, fragment etc) and view models, use InstantTaskExecutorRule

## UNIT TEST CASE CREATION PROTOCOL (TESTER)
When a specific functionalities for a given class is required, as the Tester, strictly follow this structure:
1. **General implementation rule**: This is the "Red" phase of TDD. Write a single, empty test case, for the described functionality, with name only without implementation.
2. **Test Cases Naming**: use this example "whenExecutingSearch_WithEmptyQuery_ShouldReturnEmptyList()"

## UNIT TEST CASE IMPLEMENTATION PROTOCOL (TESTER)
When asked to implement a test case, as the Tester, strictly follow this structure:
1. **Implementation**: Implement the test case method only against the system under test! do not add any implementations directly to the system under test.

## FEATURE ACCEPTANCE TEST PROTOCOL (TESTER - RED PHASE)
**Goal:** Create the package structure and class stubs to achieve a "compilable but failing" state for a **single specific scenario**.
When mapping a scenario from a Gherkin feature file:

1. **Single Scenario Enforcement**: You must ONLY map the single scenario provided in the user prompt. Ignore other scenarios in the same `.feature` file for this execution.
2. **Package Creation**: Create a package named exactly after the feature file (lowercase, no underscores) inside the `featureTest` source set.
    - *Example*: `plants_search.feature` -> `package com.alon.plantpulse.plantsdetail.featuretest.plantssearch`
3. **Step Definition Stubbing**:
   - **Naming**: Extract the annotation/tag at the head of the scenario (e.g., `@list-search-results`). Convert it to PascalCase and append "Steps".
    - **Inheritance**: Extend `com.mauriciotogneri.greencoffee.GreenCoffeeSteps`.
    - **Implementation**: Map every `Given/When/Then` from the feature file to a Kotlin function.
    - **Constraint**: Leave the function bodies **EMPTY**. Do not add logic yet.
4. **Runner Creation**:
    - **Naming**: Use the name of the Steps class and append "Runner".
    - **Structure**:
        - Use `@RunWith(ParameterizedRobolectricTestRunner::class)`. 
        - Implement the `data()` companion object pointing to `assets/[feature_name].feature`. use the scenario tag to filter scenarios.
        - The `@Test` function should simply call `start([Steps class name]Steps())`.


## FEATURE ACCEPTANCE TEST PROTOCOL (TESTER - GREEN PHASE)
**Goal:** Implement the logic inside the Step Definition stubs to make the scenarios pass.
When implementing the steps, strictly follow these technical requirements:

1. **Material 3 Search Hack (CRITICAL)**: If the scenario involves a Material 3 `SearchBar`/`SearchView`:
    - After any `click()` on the search bar, call `Shadows.shadowOf(Looper.getMainLooper()).idle()`.
    - To target the input field, resolve the internal ID at runtime:
      `val searchEditTextId = context.resources.getIdentifier("open_search_view_edit_text", "id", context.packageName)`.
2. **UI Interactions**:
    - Use Espresso `onView(...)` for all interactions.
    - Use `typeText(query)` and `pressImeActionButton()` for search execution.
3. **Verification**:
    - "Then" steps must use `check(matches(...))` to verify UI states (e.g., `isDisplayed()`, `hasChildCount()`).
4. **Context Access**: Use `androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext` for resource lookups.

## CLASS FUNCTIONALITY IMPLEMENTATION PROTOCOL (POLICY DEVELOPER)
When executing a class functionality implementation request as the Policy developer, strictly follow this structure:
1. **General implementation rule**: implement the requested functionality only in the the class specified.
2. **Dependency Definitions**: If a collaborator is missing, define it as an interface (or a pure Kotlin data class) with method signatures only. Do not provide implementation details.
3. **Use cases implementations(application layer)**: Always use interfaces to contract the composition of collaborators for the use case.
4. **Dependency Injection**: Use `@Inject` annotation in class constructor to inject dependencies .
5. **Class package mapping for application module**: Use case classes go to "usecase" package. Interfaces go to "interfaces" package. Dto data classes to "model" package.

## CLASS FUNCTIONALITY IMPLEMENTATION PROTOCOL (ANDROID DEVELOPER)
When executing a class functionality implementation request as the Android developer, strictly follow this structure:
1. **General implementation rule**: This is the "Green" phase of TDD. You must ONLY implement logic that satisfies the existing unit test cases: 1)Locate the test class for the target component. 2)Read the "Given-When-Then" steps within the test methods. 3)Implement the minimum code necessary in the production class to make those specific steps pass. 4)If a test case is empty or has a TODO, do not implement logic for it until the @Tester provides the steps. 
2. **Missing Infrastructure Stubs**: If the class requires a concrete collaborator that doesn't exist (e.g., a Room DAO or a Helper class), create the file with the class name and method signatures only. Use TODO() for the bodies.
3. **View model implementation(ui layer)**: Use Liva data as the observable state holder for the view.
4. **Android specific**: Android classes that considered entry points, and has injectable dependencies, should ise the `@AndroidEntryPoint & @OptionalInject` annotations.
5. **Ui layer,data layer, device layer**: do not create interfaces for composition to implement class functionality.
6. **Ui controller implementations(activity,fragment etc)**: always use view layout binding(created by data binding library) to call view members.
7. **Dependency Injection**: use hilt library to inject dependencies.
8. **Class package mapping for ui module**: View Model classes go to "viewmodel" package. Fragment, activity, dialog, list adapters go to "controller" package. Ui state data classes to "model" package.

## VIEW LAYOUT CREATION PROTOCOL (UI DESIGNER)
When asked to create a view layout, as the UI Designer, strictly follow this structure: 
1. **Data model**: if layout used to present an existing data model, bind it to view and use properties to present data.

