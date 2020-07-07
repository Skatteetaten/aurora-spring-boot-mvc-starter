package no.skatteetaten.aurora.mvc;

import static no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter.KORRELASJONS_ID;

import java.util.UUID;

import brave.SpanCustomizer;
import brave.baggage.BaggageField;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.propagation.TraceContext;
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon;

public class AuroraRequestParser implements HttpRequestParser {
    public static final String TAG_KORRELASJONS_ID = "aurora." + KORRELASJONS_ID.toLowerCase();

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        HttpRequestParser.DEFAULT.parse(req, context, span);

        String korrelasjonsid = RequestKorrelasjon.getId();
        if (korrelasjonsid == null) {
            korrelasjonsid = UUID.randomUUID().toString();
        }

        BaggageField.create(KORRELASJONS_ID).updateValue(context, korrelasjonsid);
        span.tag(TAG_KORRELASJONS_ID, korrelasjonsid);
    }
}
