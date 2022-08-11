package no.skatteetaten.aurora.mvc.testapp

import brave.baggage.BaggageField
import no.skatteetaten.aurora.mvc.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.mvc.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.mvc.AuroraRequestParser.MELDINGSID_FIELD
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

// The zipkin profile will connect to zipkin (run docker-compose to start local zipkin)
// The default profile will start with zipkin disabled
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
        val korrelasjonsid = BaggageField.getByName(KORRELASJONSID_FIELD)
        checkNotNull(korrelasjonsid)
        check(korrelasjonsid.value == MDC.get(KORRELASJONSID_FIELD))

        val requestHeaders = restTemplate.getForEntity<Map<String, String>>("http://localhost:8080/headers")
        return mapOf(
            "Korrelasjonsid fra Filter bean" to korrelasjonsid,
            "Request headers fra RestTemplate" to requestHeaders.body
        )
    }

    @GetMapping("/headers")
    fun getHeaders(@RequestHeader headers: HttpHeaders): Map<String, String> {
        checkNotNull(headers[KORRELASJONSID_FIELD])
        checkNotNull(headers[MELDINGSID_FIELD])
        checkNotNull(headers[KLIENTID_FIELD])
        checkNotNull(headers[USER_AGENT])
        return headers.toSingleValueMap().toMutableMap().apply {
            put("MDC-$KORRELASJONSID_FIELD", MDC.get("Korrelasjonsid"))
        }
    }
}
