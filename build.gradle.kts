plugins {
	id("base")
	id("org.springframework.boot") version "3.4.3" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	repositories {
		mavenCentral()
	}

	dependencies {
		"testImplementation"("org.springframework.boot:spring-boot-starter-test")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}