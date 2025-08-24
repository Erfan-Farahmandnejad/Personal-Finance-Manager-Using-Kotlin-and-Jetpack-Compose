// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    kotlin("kapt") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
}

