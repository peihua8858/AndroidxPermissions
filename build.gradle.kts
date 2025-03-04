import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
}

// 读取 local.properties
val localPropertiesFile = project.file("local.properties")
Properties().apply {
    load(localPropertiesFile.inputStream())
    val authToken = getProperty("auth.token")
    project.extra["jitpackAuth"] = authToken
    println("authToken: $authToken")
}