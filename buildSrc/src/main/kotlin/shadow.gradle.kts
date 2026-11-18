import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import utils.minimizeShadowedDependencies
import utils.relocateShadowedDependencies
import utils.shadowRelocationPrefix

plugins {
    java
    id("com.gradleup.shadow")
}

configurations {
    val shadowImpl = create("shadowImplementation") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }

    implementation {
        extendsFrom(shadowImpl)
    }
}

tasks.named("shadowJar", ShadowJar::class.java) {
    archiveClassifier = "shadowed"

    dependencies {
        exclude(dependency("org.jspecify:jspecify:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
    }

    project.configurations.findByName("shadowImplementation")?.let { configurations.add(it) }
    project.configurations.findByName("shadowDowngrade")?.let { configurations.add(it) }

    if (minimizeShadowedDependencies) {
        minimize()
    }

    if (relocateShadowedDependencies && shadowRelocationPrefix.isNotEmpty()) {
        enableAutoRelocation = true
        relocationPrefix = shadowRelocationPrefix
    }
}