// Root build file
plugins {
    kotlin("android") version "1.9.20" apply false
    kotlin("plugin.serialization") version "1.9.20" apply false
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "1.9.20" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
