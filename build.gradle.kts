plugins {
    `java-library`
    kotlin("jvm") version "1.6.21"
    id("no.skatteetaten.gradle.aurora") version "4.4.25"
}

aurora {
    useJavaDefaults
    useLibDefaults
    useKotlin {
        useKtLint
    }
    useSpringBoot

    features {
        auroraStarters = false
    }
}

dependencies {
    api("org.springframework.cloud:spring-cloud-starter-sleuth:3.1.3")
    api("io.zipkin.brave:brave:5.13.9")
    api("org.springframework.cloud:spring-cloud-sleuth-zipkin:3.1.3")

    api("org.springframework.boot:spring-boot-starter-web")
    api("no.skatteetaten.aurora.springboot:aurora-spring-boot-base-starter:1.3.12") {
        exclude(group = "io.zipkin.aws", module = "brave-propagation-aws")
        exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-sleuth")
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
