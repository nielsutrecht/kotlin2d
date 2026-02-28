import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "2.3.10"
    application
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.4.1"

val os = OperatingSystem.current()
val arch = System.getProperty("os.arch").lowercase()

val lwjglNatives = when {
    os.isMacOsX && (arch.contains("aarch64") || arch.contains("arm64")) -> "natives-macos-arm64"
    os.isMacOsX -> "natives-macos"
    os.isWindows -> "natives-windows"
    arch.contains("aarch64") || arch.contains("arm64") -> "natives-linux-arm64"
    else -> "natives-linux"
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
}

application {
    mainClass.set("kotlin2d.MainKt")
    // Required by GLFW on macOS: must run on the first thread
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}


