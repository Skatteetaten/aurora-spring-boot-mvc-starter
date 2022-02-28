package no.skatteetaten.aurora.mvc.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer
import no.skatteetaten.aurora.mvc.AuroraRequestParser

@TestConfiguration
open class TestConfig {

    @Bean
    open fun restTemplateBuilder(customizer: AuroraHeaderRestTemplateCustomizer) = RestTemplateBuilder(customizer)

    @Bean
    open fun restTemplate(builder: RestTemplateBuilder) = builder.build()
}

@SpringBootTest(classes = [TestConfig::class, MvcStarterApplicationConfig::class])
open class AbstractMvcStarterApplicationConfigTest {

    @Autowired(required = false)
    protected lateinit var auroraHeaderFilter: FilterRegistrationBean<*>

    @Autowired(required = false)
    protected lateinit var auroraRequestParser: AuroraRequestParser

    @Autowired
    protected lateinit var restTemplate: RestTemplate
}