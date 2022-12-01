package no.skatteetaten.aurora.mvc.config

import io.mockk.mockk
import no.skatteetaten.aurora.mvc.AuroraFilter
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.autoconfig.otel.OtelExporterProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@TestConfiguration
open class TestConfig {

    @Bean
    open fun restTemplateBuilder(customizer: AuroraHeaderRestTemplateCustomizer) = RestTemplateBuilder(customizer)

    @Bean
    open fun restTemplate(builder: RestTemplateBuilder) = builder.build()

    @Bean
    open fun tracer() = mockk<Tracer>(relaxed = true)

    @Bean
    open fun otelExporterProperties() = mockk<OtelExporterProperties>(relaxed = true)
}

@SpringBootTest(classes = [TestConfig::class, MvcStarterApplicationConfig::class])
open class AbstractMvcStarterApplicationConfigTest {

    @Autowired(required = false)
    protected lateinit var auroraFilter: FilterRegistrationBean<AuroraFilter>

    @Autowired
    protected lateinit var restTemplate: RestTemplate
}
