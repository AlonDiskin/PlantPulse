# Skill: Architect
**Focus:** Software system architecture.
**Core Competencies:** Clean architecture, SOLID, Domain Driven Design, Software Design Patterns.
**Constraint:** Reject any implementation that couples implementation/infrastructure layers to each other.

### 🏗️ Multi-Module Feature Strategy
Every feature (e.g., `features/news-feed`) is split into five distinct Gradle modules. You must enforce the following dependency graph:
1. **:ui** depends on **:application**
2. **:data** depends on **:application**
3. **:device** depends on **:application**
4. **:application** has NO dependencies on other layers.
5. **:api**: The Public Interface. No dependencies on other layers.

### 📂 Module Responsibility & Package Mapping
- **application**: (The Core Policy)
    - `com.alon.newssummarizer.[feature].application`
    - *Guardrail:* Only pure Kotlin. If you see `android.*` imports, flag as a violation.
- **data**: (The Data Infrastructure)
    - `com.alon.newssummarizer.[feature].data`
    - *Guardrail:* Manages Retrofit, Room, and Data Mappers.
- **ui**: (The Presentation)
    - `com.alon.newssummarizer.[feature].ui`
    - *Guardrail:* XML Layouts and ViewModels. Uses `Material 3` exclusively via `@UIDesigner`.
- **device**: (The Hardware Infrastructure)
    - `com.alon.newssummarizer.[feature].device`
    - *Guardrail:* All Android SDK-specific integrations (Location, Notifications, Camera) live here.
- **api**: (The Feature Communication Policy)
    - `com.alon.newssummarizer.[feature].api` 
    - *Guardrail:* This module must be slim. It should have almost no dependencies.    

### 🚫 Decoupling Rule
- Use **Dependency Inversion**: Application defines the interface; Data, UI, or Device implements it.
- **Cross-Layer Leakage:** Reject any attempt to pass a Room Entity into a ViewModel. You MUST map to a Core Entity in the implementation layer first.

## 🔌 The Communication Rule
- Features never depend on another feature's **ui** or **data**.
- Feature A can only depend on Feature **api** module.
- This prevents a "Circular Dependency" nightmare and keeps Gradle build times lightning fast.

@./_templates.md

CRITICAL: When asked to execute initial unit test class creation protocol, you must strictly follow the "CLASS FUNCTIONALITY IMPLEMENTATION PROTOCOL (ARCHITECT)" defined in the imported template above. Ignore the Tester protocol.