package no.skatteetaten.aurora.mvc.testapp

import io.opentelemetry.api.baggage.Baggage
import no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KLIENTID
import no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KORRELASJONSID
import no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_MELDINGSID
import org.slf4j.MDC
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@SpringBootApplication
open class TestMain

fun main(args: Array<String>) {
    SpringApplication.run(TestMain::class.java, *args)
}

@Configuration
open class TestConfig {
    @Bean
    open fun restTemplate(restTemplateBuilder: RestTemplateBuilder) = restTemplateBuilder.build()
}

@RestController
open class TestController(private val restTemplate: RestTemplate) {

    @GetMapping
    fun get(): Map<String, Any?> {
        val korrelasjonsid = Baggage.current().getEntryValue(HEADER_KORRELASJONSID)
        checkNotNull(korrelasjonsid)
        check(korrelasjonsid == MDC.get(HEADER_KORRELASJONSID))

        val requestHeaders = restTemplate.getForEntity<Map<String, String>>("http://localhost:8080/headers")
        return mapOf(
            "Korrelasjonsid fra Filter bean" to korrelasjonsid,
            "Request headers fra RestTemplate" to requestHeaders.body
        )
    }

    @GetMapping("/headers")
    fun getHeaders(@RequestHeader headers: HttpHeaders): Map<String, String> {
        checkNotNull(headers[HEADER_KORRELASJONSID])
        checkNotNull(headers[HEADER_MELDINGSID])
        checkNotNull(headers[HEADER_KLIENTID])
        checkNotNull(headers[USER_AGENT])
        return headers.toSingleValueMap().toMutableMap().apply {
            put("MDC-$HEADER_KORRELASJONSID", MDC.get("Korrelasjonsid"))
        }
    }
}
