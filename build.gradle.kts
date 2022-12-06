plugins {
    `java-library`
    kotlin("jvm") version "1.7.22"
    id("no.skatteetaten.gradle.aurora") version "4.5.11"
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
    api(platform("org.springframework.cloud:spring-cloud-sleuth-otel-dependencies:1.1.0"))
    api("org.springframework.cloud:spring-cloud-sleuth-instrumentation:3.1.5")
    api("org.springframework.cloud:spring-cloud-sleuth-autoconfigure:3.1.5")
    api("org.springframework.cloud:spring-cloud-starter-sleuth") {
        exclude(group = "org.springframework.cloud", module = "spring-cloud-sleuth-brave")
    }
    api("org.springframework.cloud:spring-cloud-sleuth-otel-autoconfigure") {
        exclude(group = "io.opentelemetry", module = "opentelemetry-api")
        exclude(group = "io.opentelemetry", module = "opentelemetry-semconv")
        exclude(group = "org.springframework.cloud", module = "spring-cloud-sleuth-instrumentation")
        exclude(group = "org.springframework.cloud", module = "spring-cloud-sleuth-autoconfigure:jar")
    }
    api("io.opentelemetry:opentelemetry-exporter-otlp")

    api("org.springframework.boot:spring-boot-starter-web")
    api("no.skatteetaten.aurora.springboot:aurora-spring-boot-base-starter:2.0.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.objenesis", module = "objenesis")
    }
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("no.skatteetaten.aurora:mockwebserver-extensions-kotlin:1.4.1")
    testImplementation("io.mockk:mockk:1.13.3")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveClassifier.set("boot")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")
}
