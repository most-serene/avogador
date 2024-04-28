import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.9.21"
}

group = "eu.mostserene.avogador"
version = "0.12.1"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

springBoot {
	buildInfo()
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.rauschig:jarchivelib:0.8.0")
	implementation("commons-io:commons-io:2.15.0")
	implementation("de.jplag:jplag:4.3.0")
	implementation("de.jplag:java:4.3.0")
	implementation("de.jplag:cpp2:4.3.0")
	implementation("de.jplag:python-3:4.3.0")
	implementation("io.sentry:sentry:6.16.0") // just compile should be required
	implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation(kotlin("test"))
	implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
	useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "17"
}