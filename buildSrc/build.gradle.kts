plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "ForgeMaven"
        url = uri("https://maven.minecraftforge.net/")
    }
}

dependencies {
    implementation(libs.forgeGradle)
    implementation(libs.renamer)
    implementation(libs.jvmDowngrader)
    implementation(libs.ideaExt)
    implementation(libs.shadow)
    implementation(libs.blossom)
}