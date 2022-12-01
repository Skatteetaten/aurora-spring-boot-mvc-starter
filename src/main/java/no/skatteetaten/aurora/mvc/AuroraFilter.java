package no.skatteetaten.aurora.mvc;

import static org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties.TRACING_FILTER_ORDER;

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

    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGSID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    static final String TRACE_TAG_PREFIX = "skatteetaten.";
    static final String TRACE_TAG_KORRELASJONS_ID = TRACE_TAG_PREFIX + KORRELASJONSID_FIELD.toLowerCase();
    static final String TRACE_TAG_KLIENT_ID = TRACE_TAG_PREFIX + KLIENTID_FIELD.toLowerCase();

    private final Tracer tracer;

    public AuroraFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        Span span = tracer.currentSpan();
        if (span != null) {
            String meldingsid = request.getHeader(MELDINGSID_FIELD);
            if (meldingsid != null) {
                tracer.createBaggage(MELDINGSID_FIELD, meldingsid);
            }

            String klientid = request.getHeader(KLIENTID_FIELD);
            if (klientid != null) {
                tracer.createBaggage(KLIENTID_FIELD, klientid);
                span.tag(TRACE_TAG_KLIENT_ID, klientid);
            }

            String korrelasjonsid = Optional.ofNullable(request.getHeader(KORRELASJONSID_FIELD))
                .orElse(UUID.randomUUID().toString());
            tracer.createBaggage(KORRELASJONSID_FIELD, korrelasjonsid);
            span.tag(TRACE_TAG_KORRELASJONS_ID, korrelasjonsid);

            logger.debug("All baggage: {}", tracer.getAllBaggage());
        }

        chain.doFilter(request, response);
    }
}
