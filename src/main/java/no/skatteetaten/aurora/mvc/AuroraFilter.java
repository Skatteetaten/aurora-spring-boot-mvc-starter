package no.skatteetaten.aurora.mvc;

import static org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties.TRACING_FILTER_ORDER;
import static no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KLIENTID;
import static no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KORRELASJONSID;
import static no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_MELDINGSID;
import static no.skatteetaten.aurora.mvc.AuroraConstants.TRACE_TAG_KLIENT_ID;
import static no.skatteetaten.aurora.mvc.AuroraConstants.TRACE_TAG_KORRELASJONS_ID;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.annotation.Order;

@Order(TRACING_FILTER_ORDER + 5)
public class AuroraFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuroraFilter.class);

    private final Tracer tracer;

    public AuroraFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        Span span = tracer.currentSpan();
        if (span != null) {
            String meldingsid = request.getHeader(HEADER_MELDINGSID);
            if (meldingsid != null) {
                tracer.createBaggage(HEADER_MELDINGSID, meldingsid);
            }

            String klientid = request.getHeader(HEADER_KLIENTID);
            if (klientid != null) {
                tracer.createBaggage(HEADER_KLIENTID, klientid);
                span.tag(TRACE_TAG_KLIENT_ID, klientid);
            }

            String korrelasjonsid = Optional.ofNullable(request.getHeader(HEADER_KORRELASJONSID))
                .orElse(UUID.randomUUID().toString());
            tracer.createBaggage(HEADER_KORRELASJONSID, korrelasjonsid);
            span.tag(TRACE_TAG_KORRELASJONS_ID, korrelasjonsid);

            logger.debug("All baggage: {}", tracer.getAllBaggage());
        }

        chain.doFilter(request, response);
    }
}
