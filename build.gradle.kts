plugins {
    `java-library`
    kotlin("jvm") version "1.6.21"
    id("no.skatteetaten.gradle.aurora") version "4.4.25"
}

aurora {
    useGitProperties
    useLatestVersions
    useVersions
    useSonar
    useGradleLogger
    useJavaDefaults
    useKotlinDefaults
    useSpringBoot

    features {
        auroraStarters = false
    }
}

dependencies {
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.2"))
    api("org.springframework.cloud:spring-cloud-starter-sleuth")

    api("org.springframework.boot:spring-boot-starter-web")
    api("no.skatteetaten.aurora.springboot:aurora-spring-boot-base-starter:1.3.12") {
        exclude(group = "org.osgi", module = "org.osgi.core")
        exclude(group = "io.zipkin.aws", module = "brave-propagation-aws")
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    api("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
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
