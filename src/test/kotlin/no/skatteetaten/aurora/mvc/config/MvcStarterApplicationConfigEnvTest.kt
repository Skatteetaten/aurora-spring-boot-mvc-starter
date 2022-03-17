package no.skatteetaten.aurora.mvc.config

import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.getForEntity
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mvc.AuroraRequestParser
import okhttp3.mockwebserver.MockWebServer

@TestPropertySource(properties = ["aurora.klientid=segment/mvc-starter/1.0.0"])
class MvcStarterApplicationConfigEnvTest : AbstractMvcStarterApplicationConfigTest() {

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