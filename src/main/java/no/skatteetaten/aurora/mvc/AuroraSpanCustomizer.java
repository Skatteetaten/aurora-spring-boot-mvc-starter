package no.skatteetaten.aurora.mvc;

import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.propagation.TraceContext;
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter;
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon;

public class AuroraSpanCustomizer extends FinishedSpanHandler {

    public static final String TAG_KORRELASJONS_ID = "aurora." + AuroraHeaderFilter.KORRELASJONS_ID.toLowerCase();

    @Override
    public boolean handle(TraceContext context, MutableSpan span) {
        String id = RequestKorrelasjon.getId();
        if(id != null) {
            span.tag(TAG_KORRELASJONS_ID, id);
        }

        return true;
    }
}
