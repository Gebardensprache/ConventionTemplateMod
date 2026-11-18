import utils.enableMixin
import utils.mixinConfigs
import utils.mixinRefmap

plugins {
    java
    `java-library`
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.renamer")
}

renamer.enableMixinRefmaps {
    refMap.set(mixinRefmap)
}

renamer.mixin.jar(tasks.named("shadowJar", Jar::class.java))

tasks.named<Jar>("jar") {
    manifest {
        if (enableMixin) {
            attributes["MixinConfigs"] = mixinConfigs
        }
    }
}

configurations {
    create("mixinAgent") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
}

dependencies {
    api("zone.rong:mixinbooter:10.7") { isTransitive = false }
    annotationProcessor("zone.rong:mixinbooter:10.7") { isTransitive = false }
    configurations.named("mixinAgent").get().dependencies.add(
        dependencies.create("zone.rong:mixinbooter:10.7") { isTransitive = false }
    )

    compileOnly("io.github.llamalad7:mixinextras-forge:0.5.5")

    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
    annotationProcessor("com.google.guava:guava:32.1.2-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.9")
}

if (enableMixin) {
    arrayOf("runClient", "runServer", "runObfClient", "runObfServer").forEach { taskName ->
        tasks.named(taskName, JavaExec::class.java).configure {
            doFirst {
                if (debug) {
                    jvmArgs("-javaagent:${configurations.named("mixinAgent").get().singleFile.absolutePath}")
                }
            }
        }
    }
}