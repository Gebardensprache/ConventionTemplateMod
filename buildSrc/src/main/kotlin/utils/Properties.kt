package utils

import org.gradle.api.Project
import kotlin.reflect.KProperty

// Mod Properties
val Project.archiveName: String by StringDelegate()
val Project.modGroup: String by StringDelegate()
val Project.modId: String by StringDelegate()
val Project.modName: String by StringDelegate()
val Project.modVersion: String by StringDelegate()

val Project.coreModClass: String by StringDelegate()
val Project.forceLoadAsMod: Boolean by BooleanDelegate()

val Project.accessTransformersFile: String by StringDelegate()

val Project.enableMixin: Boolean by BooleanDelegate()
val Project.mixinConfigs: String by StringDelegate()
val Project.mixinPackage: String by StringDelegate()
val Project.mixinRefmap: String by StringDelegate()

// Minecraft Properties
val Project.minecraftVersion: String by StringDelegate()
val Project.forgeVersion: String by StringDelegate()
val Project.mcpChannel: String by StringDelegate()
val Project.mcpVersion: String by StringDelegate()

val Project.userName: String by StringDelegate()

// Runtime Properties
val Project.extJavaArgs: String by StringDelegate()

// Shadow Properties
val Project.useShadowDeps: Boolean by BooleanDelegate()
val Project.minimizeShadowedDependencies: Boolean by BooleanDelegate()
val Project.relocateShadowedDependencies: Boolean by BooleanDelegate()
val Project.shadowRelocationPrefix: String by StringDelegate()

class StringDelegate {
    operator fun getValue(thisRef: Project, property: KProperty<*>): String =
            thisRef.findProperty(property.name)?.toString()
                    ?: error("Property '${property.name}' not found in gradle.properties")
}

class BooleanDelegate {
    operator fun getValue(thisRef: Project, property: KProperty<*>): Boolean =
            thisRef.findProperty(property.name)?.toString()?.toBoolean()
                    ?: error("Property '${property.name}' not found in gradle.properties")
}