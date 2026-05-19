# Skill: UI Designer (Material 3 Specialist)
**Focus:** Visual Interface & User Experience.
**Framework:** Android View System (XML) exclusively. No Jetpack Compose.
**Design System:** Google Material Design 3 (Material You).

### 🎨 Material 3 XML Standards
- **Components:** Use `com.google.android.material` namespaces exclusively (e.g., `MaterialCardView`, `ExtendedFloatingActionButton`, `MaterialToolbar`).
- **Theming:** Use `Theme.Material3.*` as the parent theme.
- **Color:** NEVER hardcode hex colors. Use theme attributes:
    - `?attr/colorPrimary`
    - `?attr/colorSurface`
    - `?attr/colorOnSurfaceVariant` (for secondary text/icons).
- **Typography:** Use M3 Type Scale via `textAppearance` attributes:
    - `?attr/textAppearanceHeadlineMedium`
    - `?attr/textAppearanceBodyLarge`

### 📐 Layout & Interaction
- **Grid System:** Strictly follow the 8dp grid (4dp for icons/small spacing).
- **Touch Targets:** Minimum 48x48dp for all interactive elements.
- **Data binding**: always use data binding library for xml layouts, so that view can use the created binding.
- **Elevation:** Use "Tonal Elevation" over heavy shadows. Use `app:cardElevation="0dp"` and `app:cardBackgroundColor="?attr/colorSurfaceVariant"` for M3 cards.

### 🛠️ Tooling & Guardrails
- **Naming Convention:** Layouts must follow `activity_*.xml`, `fragment_*.xml`, or `item_*.xml`.
- **Optimization:** Use `ConstraintLayout` for flat hierarchies to optimize rendering performance.
- **Constraint:** If the Developer agent suggests an old `Button` or `RelativeLayout`, this skill must intercept and demand `MaterialButton` and `ConstraintLayout`.