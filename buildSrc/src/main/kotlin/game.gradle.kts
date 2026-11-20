import extensions.ModDependencyExtension
import utils.accessTransformersFile
import utils.coreModClass
import utils.enableMixin
import utils.extJavaArgs
import utils.forceLoadAsMod
import utils.forgeVersion
import utils.mcpChannel
import utils.mcpVersion
import utils.minecraftVersion
import utils.modGroup
import utils.modId
import utils.modName
import utils.modVersion
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar

plugins {
    `java-library`
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.renamer")
    id("xyz.wagyourtail.jvmdowngrader")
    id("net.kyori.blossom")
}

minecraft {
    mappings(mcpChannel, mcpVersion)

    if (accessTransformersFile.isNotEmpty()) {
        accessTransformers = files(accessTransformersFile)
    }

    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("run"))
            if (extJavaArgs.isNotEmpty()) {
                jvmArgs(extJavaArgs)
            }
            if (System.getProperty("os.name").lowercase().contains("mac")) {
                jvmArgs("-XstartOnFirstThread")
            }
            jvmArgs("-ea:${modGroup}")
            if (coreModClass.isNotEmpty()) {
                systemProperty("fml.coreMods.load", "${modGroup}.${coreModClass}")
            }
            if (enableMixin) {
                systemProperty("mixin.hotSwap", "true")
                systemProperty("mixin.checks.interfaces", "true")
                systemProperty("mixin.debug.export", "true")
                try {
                    // Adds mixin config args + refmap remapping to dev runs
                    renamer.mixin.run(this)
                } catch (_: Throwable) {
                    logger.debug("mixin.run() not applicable to run config '${name}'")
                }
            }
        }

        register("client") {
            systemProperty("forge.enabledGameTestNamespaces", modId)
        }

        register("server") {
            args("--nogui")
        }
    }
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}"))

    extensions.create("mod", ModDependencyExtension::class.java, project)
}

tasks.processResources {
    filteringCharset = "UTF-8"

    inputs.property("modId", project.modId)
    inputs.property("modName", project.modName)
    inputs.property("modVersion", project.version.toString())
    inputs.property("mcVersion", project.minecraftVersion)

    filesMatching(listOf("mcmod.info", "pack.mcmeta")) {
        expand(
            "modId" to project.modId,
            "modName" to project.modName,
            "modVersion" to project.version.toString(),
            "mcVersion" to project.minecraftVersion
        )
    }
}

tasks.jar {
    archiveClassifier.set("dev-modern")
    manifest {
        if (accessTransformersFile.isNotEmpty()) {
            attributes["FMLAT"] = accessTransformersFile
        }
        if (coreModClass.isNotEmpty()) {
            attributes["FMLCorePlugin"] = coreModClass
            attributes["FMLCorePluginContainsFMLMod"] = true
            if (forceLoadAsMod) {
                attributes["ForceLoadAsMod"] = true
            }
        }
    }
}

val renameJar = renamer.classes("renameJar", tasks.named("shadeDowngradedApi", ShadeJar ::class.java)) {
    archiveClassifier.set("")
    mappings(renamer.mixin.generatedMappings)
}

val renameModernJar = renamer.classes("renameModernJar", tasks.named("jar", Jar::class.java)) {
    archiveClassifier.set("modern")
    mappings(renamer.mixin.generatedMappings)
}

tasks.assemble {
    dependsOn(renameJar)
    dependsOn(renameModernJar)
}

tasks.register<Jar>("sourcesJar") {
    description = "Assembles a jar archive containing the main sources."
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.withType(JavaExec::class.java).configureEach {
    if (name.startsWith("run")) {
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(8))
            }
        )
    }
}

renamer.mappings(minecraft.dependency.toSrg)

sourceSets {
    main {
        blossom {
            javaSources {
                property("MOD_ID", modId)
                property("MOD_NAME", modName)
                property("MOD_VERSION", modVersion)
            }
        }
    }
}