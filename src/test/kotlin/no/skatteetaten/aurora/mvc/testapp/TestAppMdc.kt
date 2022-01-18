package no.skatteetaten.aurora.mvc.testapp

import assertk.assertThat
import assertk.assertions.isNotNull
import brave.baggage.BaggageField
import brave.handler.SpanHandler
import brave.http.HttpRequestParser
import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.client.getForObject

fun main(args: Array<String>) {
    SpringApplication.run(TestAppMdc::class.java, *args)
}

@RestController
@SpringBootApplication
open class TestAppMdc {

    @Bean
    @Primary
    open fun sleuthHttpServerRequestParser2() = HttpRequestParser { request, context, _ ->
        request.header("customHeader")
            ?.let { BaggageField.create("customField").updateValue(context, it) }
    }

    @Bean
    open fun spanHandler(): SpanHandler = SpanHandler.NOOP

    @GetMapping
    fun getMdc(): Map<String, String> = MDC.getCopyOfContextMap()
}

@SpringBootTest(classes = [TestAppMdc::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Requester {

    @LocalServerPort
    private lateinit var port: String

    @Test
    fun `test me baby`() {
        val restTemplate = RestTemplate()
        repeat(10) {
            val json : JsonNode = restTemplate.getForObject("http://localhost:$port")
            val korrelasjonsid = json.at("/Korrelasjonsid").textValue()
            assertThat(korrelasjonsid).isNotNull()
        }
    }

}