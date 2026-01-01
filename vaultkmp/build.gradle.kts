import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = property("GROUP").toString()
version = property("VERSION_NAME").toString()

kotlin {
    explicitApi()

    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "VaultKMP"
            isStatic = true
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotest.assertions.core)
        }

        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.security.crypto)
        }
    }
}

android {
    namespace = "in.sitharaj.vaultkmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Publishing configuration
publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set(property("POM_NAME").toString())
            description.set(property("POM_DESCRIPTION").toString())
            url.set(property("POM_URL").toString())

            licenses {
                license {
                    name.set(property("POM_LICENCE_NAME").toString())
                    url.set(property("POM_LICENCE_URL").toString())
                }
            }

            developers {
                developer {
                    id.set(property("POM_DEVELOPER_ID").toString())
                    name.set(property("POM_DEVELOPER_NAME").toString())
                }
            }

            scm {
                url.set(property("POM_SCM_URL").toString())
                connection.set(property("POM_SCM_CONNECTION").toString())
                developerConnection.set(property("POM_SCM_DEV_CONNECTION").toString())
            }
        }
    }

    repositories {
        maven {
            name = "localStaging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

// Javadoc jar using Dokka
val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.named("dokkaHtml"))
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml").map { it.outputs.files })
}

// Attach javadoc to publications
publishing {
    publications.withType<MavenPublication> {
        artifact(javadocJar)
    }
}

// Generate bundle zip for manual upload to central.sonatype.com
val zipBundle by tasks.registering(Zip::class) {
    dependsOn(tasks.withType<PublishToMavenRepository>().matching { it.name.contains("LocalStaging") })
    from(layout.buildDirectory.dir("staging-deploy"))
    archiveFileName.set("vaultkmp-bundle.zip")
    destinationDirectory.set(layout.buildDirectory.dir("bundle"))
}

signing {
    sign(publishing.publications)
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(tasks.withType<Sign>())
}
