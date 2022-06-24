package no.skatteetaten.aurora.mvc

import java.net.URI
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier
import org.springframework.http.HttpMethod
import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import brave.baggage.BaggageField

class AuroraHeaderRestTemplateCustomizerTest {
    @Test
    fun `If KorrelasjonsId is declared, but not instantiated, give it a new value`() {
        val request = ClientHttpRequestFactorySupplier().get().createRequest(URI("http://localhost"), HttpMethod.GET)
        val auroraHeaderRestTemplateCustomizer = object : AuroraHeaderRestTemplateCustomizer("test") {
            override fun getBaggageField(): BaggageField {
                return BaggageField.create(AuroraRequestParser.KORRELASJONSID_FIELD)
            }
        }
        auroraHeaderRestTemplateCustomizer.addCorrelationId(request)
        assertThat(request.headers[AuroraRequestParser.KORRELASJONSID_FIELD]!![0]).isNotNull().isNotEmpty()
    }
}
