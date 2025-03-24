plugins {
	java
	id("org.springframework.boot") version "3.5.0-M3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.aleksey"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "2025.0.0-M2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("io.micrometer:micrometer-tracing-bridge-brave")
	implementation("io.zipkin.reporter2:zipkin-reporter-brave:3.5.0")
	implementation("commons-codec:commons-codec:1.18.0")
	implementation("org.json:json:20250107")
	implementation("net.logstash.logback:logstash-logback-encoder:8.0")

	compileOnly("org.projectlombok:lombok")

	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}