package no.skatteetaten.aurora.config

import assertk.assertThat
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.servlet.FilterRegistrationBean

@SpringBootTest(classes = [MvcStarterApplicationConfig::class])
class MvcStarterApplicationConfigTest {

    @Autowired
    private lateinit var auroraHeaderFilter: FilterRegistrationBean<*>

    @Test
    fun `Initialize MDC filter`() {
        assertThat(auroraHeaderFilter).isNotNull()
    }

}