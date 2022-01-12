package no.skatteetaten.aurora.mvc.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer
import no.skatteetaten.aurora.mvc.AuroraRequestParser
import no.skatteetaten.aurora.mvc.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.mvc.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.mvc.AuroraRequestParser.MELDINGID_FIELD
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@TestConfiguration
open class TestConfig {

    @Bean
    open fun restTemplateBuilder(customizer: AuroraHeaderRestTemplateCustomizer) = RestTemplateBuilder(customizer)

    @Bean
    open fun restTemplate(builder: RestTemplateBuilder) = builder.build()
}

@SpringBootTest(classes = [TestConfig::class, MvcStarterApplicationConfig::class])
class MvcStarterApplicationConfigTest {

    @Autowired(required = false)
    private lateinit var auroraHeaderFilter: FilterRegistrationBean<*>

    @Autowired(required = false)
    private lateinit var auroraRequestParser: AuroraRequestParser

    @Autowired
    private lateinit var restTemplate: RestTemplate

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
        assertThat(headers[KORRELASJONSID_FIELD])
            .isNotNull()
            .isNotEmpty()

        assertThat(headers[KLIENTID_FIELD])
            .isNotNull()
            .isEqualTo("mvc-starter")

        assertThat(headers[MELDINGID_FIELD])
            .isNotNull()
            .isNotEmpty()

        assertThat(headers[HttpHeaders.USER_AGENT])
            .isNotNull()
            .isEqualTo("mvc-starter")
    }
}