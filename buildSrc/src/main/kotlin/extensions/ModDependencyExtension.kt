package extensions

import net.minecraftforge.renamer.gradle.RenamerExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.dependencies.DependencyVariant
import org.gradle.api.provider.Provider

abstract class ModDependencyExtension(project: Project) {

    private val renamer by lazy { project.extensions.getByType(RenamerExtension::class.java) }

    /**
     * Deobfuscates a dependency via renamer extension.
     * Handles [org.gradle.api.provider.Provider], [org.gradle.api.artifacts.Dependency], [org.gradle.api.internal.artifacts.dependencies.DependencyVariant], and raw notation strings.
     */
    fun of(dependencyNotation: Any): Any {
        return when (dependencyNotation) {
            is Dependency -> of(dependencyNotation)
            is DependencyVariant -> of(dependencyNotation)
            is Provider<*> -> of(dependencyNotation.get())
            else -> renamer.dependency(dependencyNotation.toString()) { accessTransformers.set(false) }
        }
    }

    fun of(provider: Provider<out Dependency>): Provider<Dependency> {
        return of(provider.get())
    }

    fun of(dependency: Dependency): Provider<Dependency> {
        var depSpec = "${dependency.group}:${dependency.name}:${dependency.version}"
        if (dependency is DependencyVariant) {
            depSpec = "$depSpec:${(dependency as DependencyVariant).classifier}"
        }
        return renamer.dependency(depSpec) { accessTransformers.set(false) }
    }
}