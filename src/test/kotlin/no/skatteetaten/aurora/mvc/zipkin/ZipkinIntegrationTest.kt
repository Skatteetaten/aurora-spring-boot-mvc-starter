package no.skatteetaten.aurora.mvc.zipkin

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.support.expected
import brave.sampler.Sampler
import com.fasterxml.jackson.databind.JsonNode
import no.skatteetaten.aurora.mvc.AuroraRequestParser.TAG_KORRELASJONS_ID
import no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig
import no.skatteetaten.aurora.mvc.request.RequestTestMain
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.awaitility.kotlin.withPollDelay
import org.awaitility.kotlin.withPollInterval
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

@TestConfiguration
open class TestConfig {
    @Bean
    open fun defaultSampler(): Sampler {
        return Sampler.ALWAYS_SAMPLE
    }

    @Bean
    open fun restTemplate(builder: RestTemplateBuilder) = builder.build()
}

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@Testcontainers
@SpringBootTest(
    classes = [TestConfig::class, RequestTestMain::class, MvcStarterApplicationConfig::class],
    properties = ["aurora.mvc.header.span.interceptor.enabled=true"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ZipkinIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: RestTemplate

    companion object {
        @Container
        val zipkin: KGenericContainer = KGenericContainer(
            "openzipkin/zipkin-slim:2"
        )
            .withExposedPorts(9411)
            .waitingFor(Wait.forHttp("/zipkin"))

        @JvmStatic
        @DynamicPropertySource
        fun zipkinProperties(registry: DynamicPropertyRegistry) {
            zipkin.start()
            registry.add("spring.zipkin.base-url") {
                "http://${zipkin.host}:${zipkin.firstMappedPort}"
            }
        }
    }

    @Test
    fun `Request registers tracing data in zipkin`() {
        val traceRequest = restTemplate.getForEntity<Map<String, String>>("http://localhost:$port/test")

        val spans = await()
            .withPollDelay(Duration.ofSeconds(1)) // Wait initial
            .withPollInterval(Duration.ofMillis(100)) // Wait before retrying
            .untilCallTo {
                restTemplate.getForEntity<JsonNode>("http://localhost:${zipkin.firstMappedPort}/api/v2/spans?serviceName=mvc-starter")
            } has { body!!.size() > 0 }

        val traces = restTemplate.getForEntity<JsonNode>("http://localhost:${zipkin.firstMappedPort}/api/v2/traces")

        assertThat(traceRequest.statusCode).isEqualTo(OK)
        assertThat(spans.statusCode).isEqualTo(OK)
        assertThat(traces.statusCode).isEqualTo(OK)
        assertThat(traces).containsKorrelasjonsidTag()
    }

    private fun Assert<ResponseEntity<JsonNode>>.containsKorrelasjonsidTag() = given { actual ->
        if (actual.body!!.findValues("tags").any { it.has(TAG_KORRELASJONS_ID) }) return
        expected("response to contain tag $TAG_KORRELASJONS_ID")
    }
}