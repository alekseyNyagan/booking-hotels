plugins {
    id("base")
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

val springCloudVersion = "2025.1.3"

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "com.aleksey"
    version = "0.0.1-SNAPSHOT"

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    repositories {
        mavenCentral()
    }

    extensions.configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }

    dependencies {
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    plugins.withType<org.springframework.boot.gradle.plugin.SpringBootPlugin> {
        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
            imageName.set("alekseynyagan607/${project.name}:latest")
            builder.set("paketobuildpacks/builder-jammy-base:latest")
            environment.set(mapOf("BP_JVM_VERSION" to "25"))
        }
    }
}
