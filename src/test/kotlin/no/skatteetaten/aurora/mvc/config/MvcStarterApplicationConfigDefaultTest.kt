package no.skatteetaten.aurora.mvc.config

import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.web.client.getForEntity
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mvc.AuroraRequestParser
import okhttp3.mockwebserver.MockWebServer

class MvcStarterApplicationConfigDefaultTest : AbstractMvcStarterApplicationConfigTest() {

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