package no.skatteetaten.aurora.mvc

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.mvc.AuroraFilter.KORRELASJONSID_FIELD
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier
import org.springframework.http.HttpMethod
import java.net.URI

class AuroraHeaderRestTemplateCustomizerTest {
    @Test
    fun `If KorrelasjonsId is declared, but not instantiated, give it a new value`() {
        val request = ClientHttpRequestFactorySupplier().get().createRequest(URI("http://localhost"), HttpMethod.GET)
        val auroraHeaderRestTemplateCustomizer = object : AuroraHeaderRestTemplateCustomizer("test") {
            override fun getBaggageField(): String? {
                return null
            }
        }
        auroraHeaderRestTemplateCustomizer.addCorrelationId(request)
        assertThat(request.headers[KORRELASJONSID_FIELD]!![0]).isNotNull().isNotEmpty()
    }
}
