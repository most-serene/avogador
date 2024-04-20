plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
}

group = "eu.mostserene.avogador"
version = "0.12.0"

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
	implementation("commons-io:commons-io:2.15.0")
	implementation("org.rauschig:jarchivelib:0.8.0")
	implementation("org.apache.commons:commons-compress:1.24.0")
	implementation("io.sentry:sentry:6.16.0") // just compile should be required
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("com.google.guava:guava:31.1-jre")
	implementation("javax.xml.bind:jaxb-api:2.3.1")
	implementation("com.sun.xml.bind:jaxb-impl:2.2.5-b10")
	implementation("org.javatuples:javatuples:1.2")
	implementation("net.minidev:json-smart:2.4.11")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
