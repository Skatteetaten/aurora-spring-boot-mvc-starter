package no.skatteetaten.aurora.mvc.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.opentelemetry.api.baggage.Baggage
import no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KLIENTID
import no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KORRELASJONSID
import no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_MELDINGSID
import no.skatteetaten.aurora.mvc.AuroraFilter
import no.skatteetaten.aurora.mvc.AuroraFilter.*
import no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.UUID

@SpringBootApplication
open class RequestTestMain

@RestController
open class RequestTestController {

    @GetMapping("/test")
    fun getText() = mapOf(
        "mdc_Korrelasjonsid" to MDC.get(HEADER_KORRELASJONSID),
        "mdc_Klientid" to MDC.get(HEADER_KLIENTID),
        "mdc_Meldingsid" to MDC.get(HEADER_MELDINGSID),
        "span" to Baggage.current().getEntryValue(HEADER_KORRELASJONSID)
    ).also {
        LoggerFactory.getLogger(RequestTestController::class.java).info("Clearing MDC, content: $it")
        MDC.clear()
    }
}

class RequestTest {

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, MvcStarterApplicationConfig::class],
        properties = [
            "aurora.mvc.header.filter.enabled=true"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class FilterEnabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `MDC and BaggageField contains Korrelasjonsid`() {
            val response = sendRequest(port)

            assertThat(response["mdc_Korrelasjonsid"]).isNotNull().isNotEmpty()
            assertThat(response["span"]).isNotNull().isNotEmpty()
        }

        @Test
        fun `MDC and BaggageField is equal`() {
            val response = sendRequest(port)
            assertThat(response["mdc_Korrelasjonsid"]).isEqualTo(response["span"])
        }

        @Test
        fun `Klientid from request is put on MDC`() {
            val response = sendRequest(port, mapOf("Klientid" to "klient/1.2"))
            assertThat(response["mdc_Klientid"]).isEqualTo("klient/1.2")
        }

        @Test
        fun `Korrelasjonsid from request is put on MDC`() {
            val korrelasjonsId = UUID.randomUUID().toString()
            val response = sendRequest(port, mapOf("Korrelasjonsid" to korrelasjonsId))
            assertThat(response["mdc_Korrelasjonsid"]).isEqualTo(korrelasjonsId)
        }

        @Test
        fun `Meldingsid from request is put on MDC`() {
            val meldingsId = UUID.randomUUID().toString()
            val response = sendRequest(port, mapOf("Meldingsid" to meldingsId))
            assertThat(response["mdc_Meldingsid"]).isEqualTo(meldingsId)
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, MvcStarterApplicationConfig::class],
        properties = [
            "aurora.mvc.header.filter.enabled=false"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class FilterDisabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `MDC and Korrelasjonsid is null`() {
            val response = sendRequest(port)

            assertThat(response["mdc_Korrelasjonsid"]).isNull()
            assertThat(response["span"]).isNull()
        }
    }

    fun sendRequest(port: Int, headers: Map<String, String> = emptyMap()) =
        RestTemplate().exchange<Map<String, String>>(
            "http://localhost:$port/test",
            HttpMethod.GET,
            HttpEntity(null, LinkedMultiValueMap(headers.mapValues { listOf(it.value) }))
        ).body!!
}
