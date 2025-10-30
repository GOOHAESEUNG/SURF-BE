plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.tavemakers"
version = "0.0.1-SNAPSHOT"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")      // MVC (Controller)
    implementation("org.springframework.boot:spring-boot-starter-webflux")  // WebClient
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // TSID
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.8.3")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.4")

    // Data Jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // MySQL
    runtimeOnly("com.mysql:mysql-connector-j")

    // .env 자동 로딩
    implementation("me.paulschwarz:spring-dotenv:3.0.0")

    // Spring AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.10.1")

    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Aop
    implementation ("org.springframework.boot:spring-boot-starter-aop")
    implementation ("com.fasterxml.jackson.core:jackson-databind")

    // AWS S3
    implementation ("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

}

// log4j2 사용을 위해 추가
//configurations.all {
//    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
//}

tasks.test {
    useJUnitPlatform()
}
