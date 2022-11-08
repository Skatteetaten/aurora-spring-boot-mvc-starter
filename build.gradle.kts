plugins {
    `java-library`
    kotlin("jvm") version "1.7.20"
    id("no.skatteetaten.gradle.aurora") version "4.5.10"
}

aurora {
    useLibDefaults
    useSpringBoot
    useVersions

    features {
        auroraStarters = false
    }

    versions {
         javaSourceCompatibility = "1.8"
    }
}

dependencies {
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.5"))
    api("org.springframework.cloud:spring-cloud-starter-sleuth")
    api("org.springframework.cloud:spring-cloud-sleuth-zipkin")

    // strict versions to avoid conflicts
    api("io.zipkin.brave:brave") {
        version { strictly("5.13.9") }
    }

    api("org.springframework.boot:spring-boot-starter-web")
    api("no.skatteetaten.aurora.springboot:aurora-spring-boot-base-starter:feature_AOS_7215-SNAPSHOT")
    api("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.4.1")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveClassifier.set("boot")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")
}
