package no.skatteetaten.aurora.mvc.testapp

import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter.*
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer.*
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
    fun get(): Map<String, Any> {
        val korrelasjonsid = RequestKorrelasjon.getId()
        checkNotNull(korrelasjonsid)

        val requestHeaders = restTemplate.getForEntity<Map<String, String>>("http://localhost:8080/headers")
        return mapOf(
            "Korrelasjonsid fra Filter bean" to korrelasjonsid,
            "Request headers fra RestTemplate" to requestHeaders.headers
        )
    }

    @GetMapping("/headers")
    fun getHeaders(@RequestHeader headers: HttpHeaders): HttpHeaders {
        checkNotNull(headers[KORRELASJONS_ID])
        checkNotNull(headers[MELDINGS_ID])
        checkNotNull(headers[KLIENT_ID])
        checkNotNull(headers[USER_AGENT])
        return headers
    }
}