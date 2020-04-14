package no.skatteetaten.aurora.mvc;

import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.propagation.TraceContext;
import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter;
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon;

public class AuroraSpanCustomizer extends FinishedSpanHandler {

    @Override
    public boolean handle(TraceContext context, MutableSpan span) {
        String id = RequestKorrelasjon.getId();
        if(id != null) {
            span.tag("aurora." + AuroraHeaderFilter.KORRELASJONS_ID.toLowerCase(), id);
        }

        return true;
    }
}
