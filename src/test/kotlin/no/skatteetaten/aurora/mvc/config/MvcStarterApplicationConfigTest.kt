package no.skatteetaten.aurora.mvc.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mvc.AuroraRequestParser
import no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig.HEADER_ORGID
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.zipkin2.ZipkinRestTemplateProvider
import org.springframework.http.HttpHeaders
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.getForEntity

class MvcStarterApplicationConfigTest {
    @Nested
    inner class DefaultTest : AbstractMvcStarterApplicationConfigTest() {

        @Test
        fun `Initialize request parser`() {
            assertThat(auroraRequestParser).isNotNull()
        }

        @Test
        fun `Set Aurora header on request`() {
            val server = MockWebServer()
            val request = server.execute("test") {
                restTemplate.getForEntity<String>("http://localhost:${server.port}")
            }.first()

            val headers = request?.headers!!
            assertThat(headers[AuroraRequestParser.KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
            assertThat(headers[AuroraRequestParser.KLIENTID_FIELD]).isNotNull().isEqualTo("mvc-starter")
            assertThat(headers[AuroraRequestParser.MELDINGSID_FIELD]).isNotNull().isNotEmpty()
            assertThat(headers[HttpHeaders.USER_AGENT]).isNotNull().isEqualTo("mvc-starter")
        }
    }

    @Nested
    @TestPropertySource(properties = ["aurora.klientid=segment/mvc-starter/1.0.0"])
    inner class KlientIdEnvTest : AbstractMvcStarterApplicationConfigTest() {

        @Test
        fun `Set KlientID from env`() {
            val server = MockWebServer()
            val request = server.execute("test") {
                restTemplate.getForEntity<String>("http://localhost:${server.port}")
            }.first()

            val headers = request?.headers!!
            assertThat(headers[AuroraRequestParser.KLIENTID_FIELD]).isNotNull().isEqualTo("segment/mvc-starter/1.0.0")
            assertThat(headers[HttpHeaders.USER_AGENT]).isNotNull().isEqualTo("segment/mvc-starter/1.0.0")
        }
    }

    @Nested
    @TestPropertySource(properties = ["app.version=1.0.0"])
    inner class VersionEnvTest : AbstractMvcStarterApplicationConfigTest() {

        @Test
        fun `Set fallback klientId`() {
            val server = MockWebServer()
            val request = server.execute("test") {
                restTemplate.getForEntity<String>("http://localhost:${server.port}")
            }.first()

            val headers = request?.headers!!
            assertThat(headers[AuroraRequestParser.KLIENTID_FIELD]).isNotNull().isEqualTo("mvc-starter/1.0.0")
            assertThat(headers[HttpHeaders.USER_AGENT]).isNotNull().isEqualTo("mvc-starter/1.0.0")
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    @TestPropertySource(
        properties = [
            "trace.auth.username=test123",
            "trace.auth.password=test234",
            "aurora.klientid=segment/mvc-starter/1.0.0"]
    )
    inner class TraceAuthEnabled : AbstractMvcStarterApplicationConfigTest() {
        @Autowired(required = false)
        private var provider: ZipkinRestTemplateProvider? = null
        private val server = MockWebServer()

        @BeforeEach
        fun setUp() {
            server.start(8888)
        }

        @AfterEach
        fun tearDown() {
            runCatching { server.shutdown() }
        }

        @Test
        fun `Trace auth enabled and default Authorization header is set`() {
            val request = server.execute(MockResponse()) {
                provider?.zipkinRestTemplate()?.getForEntity<Unit>("http://localhost:8888")
            }.first()!!

            assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)!!.startsWith("Basic")).isTrue()
        }

        @Test
        fun `OrgId header set`() {
            val request = server.execute(MockResponse()) {
                provider?.zipkinRestTemplate()?.getForEntity<Unit>("http://localhost:8888")
            }.first()!!

            assertThat(request.getHeader(HEADER_ORGID)).isEqualTo("segment")
        }
    }
}



