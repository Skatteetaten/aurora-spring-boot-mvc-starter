package no.skatteetaten.aurora.mvc.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import brave.baggage.BaggageField
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter.KORRELASJONS_ID
import no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@SpringBootApplication
open class RequestTestMain

@RestController
open class RequestTestController {

    @GetMapping("/test")
    fun getText() = mapOf(
        "mdc" to MDC.get(KORRELASJONS_ID),
        "span" to BaggageField.getByName(KORRELASJONS_ID).value
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
            "spring.zipkin.enabled=true",
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

            assertThat(response["mdc"]).isNotNull().isNotEmpty()
            assertThat(response["span"]).isNotNull().isNotEmpty()
        }

        @Test
        fun `MDC and BaggageField is equal`() {
            val response = sendRequest(port)
            assertThat(response["mdc"]).isEqualTo(response["span"])
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, MvcStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=true",
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

            assertThat(response["mdc"]).isNull()
            assertThat(response["span"]).isNull()
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, MvcStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=false",
            "aurora.mvc.header.filter.enabled=true"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class ZipkinDisabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `Korrelasjonsid is not set with filter if zipkin disabled`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNull()
            assertThat(response["span"]).isNull()
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, MvcStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=false",
            "aurora.mvc.header.filter.enabled=false"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class ZipkinAndFilterDisabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `Korrelasjonsid is null`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNull()
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