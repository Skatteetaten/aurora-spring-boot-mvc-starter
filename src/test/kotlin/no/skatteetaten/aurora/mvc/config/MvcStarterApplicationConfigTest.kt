package no.skatteetaten.aurora.mvc.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mvc.AuroraFilter.KLIENTID_FIELD
import no.skatteetaten.aurora.mvc.AuroraFilter.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.mvc.AuroraFilter.MELDINGSID_FIELD
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.getForEntity

class MvcStarterApplicationConfigTest {
    @Nested
    inner class DefaultTest : AbstractMvcStarterApplicationConfigTest() {

        @Test
        fun `Initialize aurora filter`() {
            assertThat(auroraFilter.filter).isNotNull()
        }

        @Test
        fun `Set Aurora header on request`() {
            val server = MockWebServer()
            val request = server.execute("test") {
                restTemplate.getForEntity<String>("http://localhost:${server.port}")
            }.first()

            val headers = request?.headers!!
            assertThat(headers[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
            assertThat(headers[KLIENTID_FIELD]).isNotNull().isEqualTo("mvc-starter")
            assertThat(headers[MELDINGSID_FIELD]).isNotNull().isNotEmpty()
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
            assertThat(headers[KLIENTID_FIELD]).isNotNull().isEqualTo("segment/mvc-starter/1.0.0")
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
            assertThat(headers[KLIENTID_FIELD]).isNotNull().isEqualTo("mvc-starter/1.0.0")
            assertThat(headers[HttpHeaders.USER_AGENT]).isNotNull().isEqualTo("mvc-starter/1.0.0")
        }
    }
}
