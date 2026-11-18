import utils.archiveName
import utils.modGroup
import utils.modVersion

plugins {
    `java-library`
    id("xyz.wagyourtail.jvmdowngrader")
}

group = modGroup
version = modVersion

base {
    archivesName = archiveName
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

jvmdg {
    downgradeTo = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

configurations {
    val localRuntime = create("localRuntime") {
        isCanBeConsumed = false
    }

    runtimeClasspath {
        extendsFrom(localRuntime)
    }

    val shadowDowngrade = create("shadowDowngrade") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }

    compileOnly {
        extendsFrom(shadowDowngrade)
    }
}

jvmdg.apply {
    configurations.findByName("shadowDowngrade")?.let { dg(it) }
}