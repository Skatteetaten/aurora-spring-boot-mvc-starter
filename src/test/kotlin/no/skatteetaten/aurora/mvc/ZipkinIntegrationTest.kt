package no.skatteetaten.aurora.mvc

import assertk.assertThat
import brave.sampler.Sampler
import com.fasterxml.jackson.databind.JsonNode
import no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

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
        val zipkin = KGenericContainer("openzipkin/zipkin-slim:2")
            .withExposedPorts(9411)

        @JvmStatic
        @DynamicPropertySource
        fun zipkinProperties(registry: DynamicPropertyRegistry) {
            zipkin.start()
            registry.add("spring.zipkin.baseUrl") {
                "http://${zipkin.host}:${zipkin.firstMappedPort}"
            }
        }
    }

    @Test
    fun `Request registers tracing data in zipkin`() {
        val response = restTemplate.getForEntity<Map<String, String>>("http://localhost:$port/test")
        val zipkinResponse =
            restTemplate.getForEntity<JsonNode>("http://localhost:${zipkin.firstMappedPort}/api/v2/spans?serviceName=mvc-starter")
        println(zipkinResponse)
    }
}