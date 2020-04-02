package no.skatteetaten.aurora.mvc.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter.KORRELASJONS_ID
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer.KLIENT_ID
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer.MELDINGS_ID
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
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

    @Autowired
    private lateinit var auroraHeaderFilter: FilterRegistrationBean<*>

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Test
    fun `Initialize MDC filter`() {
        assertThat(auroraHeaderFilter).isNotNull()
    }

    @Test
    fun `Set Aurora header on request`() {
        val server = MockWebServer()
        val request = server.execute("test") {
            restTemplate.getForEntity<String>("http://localhost:${server.port}")
        }.first()

        val headers = request?.headers!!
        assertThat(headers[KORRELASJONS_ID])
            .isNotNull()
            .isNotEmpty()

        assertThat(headers[KLIENT_ID])
            .isNotNull()
            .isEqualTo("mvc-starter")

        assertThat(headers[MELDINGS_ID])
            .isNotNull()
            .isNotEmpty()
    }

    @Test
    fun `Use same Korrelasjonsid if already set`() {
        RequestKorrelasjon.setId("123")
        val server = MockWebServer()
        val request = server.execute("test") {
            restTemplate.getForEntity<String>("http://localhost:${server.port}")
        }.first()

        val headers = request?.headers!!
        assertThat(headers[KORRELASJONS_ID])
            .isNotNull()
            .isEqualTo("123")

        RequestKorrelasjon.setId(null)
    }
}