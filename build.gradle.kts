// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
<<<<<<< HEAD
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
=======
    alias(libs.plugins.jetbrains.kotlin.android) apply false
>>>>>>> c0593dd9c4c276f6b238b6959e60f6131d473651
}