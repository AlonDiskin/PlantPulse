// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

// Custom task that runs all local unit test for this app
tasks.register("unitTests") {
    subprojects.forEach { subproject ->
        if (subproject.name == "app") {
            dependsOn(subproject.tasks.named("testDebugUnitTest"))
        }

        if (subproject.plugins.findPlugin("java-library") != null) {
            dependsOn(subproject.tasks.named("test"))
        }
        if (subproject.plugins.findPlugin("com.android.library") != null && subproject.name != "featureTest") {
            dependsOn(subproject.tasks.named("testDebugUnitTest"))
        }
    }
}

// Custom task that runs all feature/acceptance tests (integration test in scope) for this app
tasks.register("featureTests") {
    subprojects.forEach { subproject ->
        if (subproject.plugins.findPlugin("com.android.library") != null && subproject.name == "featureTest") {
            dependsOn(subproject.tasks.named("testDebugUnitTest"))
        }
    }
}