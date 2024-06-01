// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.android.library") version "7.3.1" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.gms.google-services:com.google.gms.google-services.gradle.plugin:4.4.1")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}