plugins {
    `java-library`
    kotlin("jvm") version "1.7.10"
    id("no.skatteetaten.gradle.aurora") version "4.5.6"
}

aurora {
    useLibDefaults
    useSpringBoot
    useVersions

    features {
        auroraStarters = false
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("no.skatteetaten.aurora.springboot:aurora-spring-boot-base-starter:1.4.3")
    api("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.3.1")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveClassifier.set("boot")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")
}
