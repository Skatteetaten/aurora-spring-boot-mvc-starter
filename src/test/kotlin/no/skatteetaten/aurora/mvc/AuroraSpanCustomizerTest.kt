package no.skatteetaten.aurora.mvc

import assertk.assertThat
import assertk.assertions.isEqualTo
import brave.Tracing
import brave.handler.FinishedSpanHandler
import brave.handler.MutableSpan
import brave.propagation.TraceContext
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon
import no.skatteetaten.aurora.mvc.AuroraSpanCustomizer.*
import org.junit.jupiter.api.Test

class AuroraSpanCustomizerTest {

    private val spans = mutableListOf<MutableSpan>()
    private val tracer = Tracing.newBuilder()
        .addFinishedSpanHandler(AuroraSpanCustomizer())
        .addFinishedSpanHandler(object: FinishedSpanHandler() {
            override fun handle(c: TraceContext?, s: MutableSpan?): Boolean {
                spans.add(s!!)
                return true
            }
        })
        .build().tracer()

    @Test
    fun `Set Korrelasjonsid tag as part of the span`() {
        RequestKorrelasjon.setId("123")

        tracer.nextSpan().apply {
            start()
            finish()
        }

        assertThat(spans.first().tag(TAG_KORRELASJONS_ID)).isEqualTo("123")
    }
}