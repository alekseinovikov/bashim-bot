import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    id("com.google.cloud.tools.jib") version "3.1.4"
}

group = "me.alekseinovikov.bashim"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("commons-codec:commons-codec:1.15")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.5")

    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


jib {
    from {
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }

            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }
    to {
        auth {
            username = System.getenv("DOCKER_HUB_USER_NAME")
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
        image = "alekseinovikov/bashim-bot"

        val tagNameEnv = System.getenv("TAG_NAME")
        tags = if (tagNameEnv.isNullOrBlank()) setOf("develop") else setOf(tagNameEnv, "latest")
    }
}
