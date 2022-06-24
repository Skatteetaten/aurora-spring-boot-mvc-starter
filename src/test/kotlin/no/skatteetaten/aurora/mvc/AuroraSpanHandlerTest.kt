package no.skatteetaten.aurora.mvc

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.skatteetaten.aurora.mvc.AuroraSpanHandler.TRACE_TAG_AURORA_KLIENTID
import no.skatteetaten.aurora.mvc.AuroraSpanHandler.TRACE_TAG_CLUSTER
import no.skatteetaten.aurora.mvc.AuroraSpanHandler.TRACE_TAG_ENVIRONMENT
import no.skatteetaten.aurora.mvc.AuroraSpanHandler.TRACE_TAG_POD
import no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig
import no.skatteetaten.aurora.mvc.request.RequestTestMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@SpringBootTest(
    classes = [RequestTestMain::class, MvcStarterApplicationConfig::class],
    properties = [
        "spring.zipkin.enabled=true",
        "aurora.webflux.header.filter.enabled=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class AuroraSpanHandlerTest {
    @LocalServerPort
    private var port: Int = 0

    private val server = MockWebServer()

    @BeforeEach
    fun setUp() {
        server.start(9411)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get trace tags from request`() {
        server.enqueue(MockResponse())

        RestTemplate().getForEntity<Unit>("http://localhost:$port/mdc")

        val request = server.takeRequest()
        val body = request.body.readUtf8()

        assertThat(body.getTag(TRACE_TAG_CLUSTER)).isNotEmpty()
        assertThat(body.getTag(TRACE_TAG_POD)).isEqualTo("test-123")
        assertThat(body.getTag(TRACE_TAG_ENVIRONMENT)).isEqualTo("test-dev")
        assertThat(body.getTag(TRACE_TAG_AURORA_KLIENTID)).isEmpty()
    }

    private fun String.getTag(tagName: String) =
        jacksonObjectMapper()
            .readTree(this)
            .get(0)
            .at("/tags")[tagName]?.toString()
            ?.removeSurrounding("\"") ?: ""
}
