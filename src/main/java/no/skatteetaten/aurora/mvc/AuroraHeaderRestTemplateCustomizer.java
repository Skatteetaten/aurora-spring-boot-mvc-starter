package no.skatteetaten.aurora.mvc;

import static no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KLIENTID;
import static no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_KORRELASJONSID;
import static no.skatteetaten.aurora.mvc.AuroraConstants.HEADER_MELDINGSID;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestTemplate;

import io.opentelemetry.api.baggage.Baggage;

public class AuroraHeaderRestTemplateCustomizer implements RestTemplateCustomizer {
    private final String appName;

    public AuroraHeaderRestTemplateCustomizer(String appName) {
        this.appName = appName;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            addCorrelationId(request);
            addClientId(request);
            addMessageId(request);
            return execution.execute(request, body);
        });
    }

    protected void addCorrelationId(HttpRequest request) {
        String korrelasjonsid = Optional.ofNullable(getBaggageField())
            .orElseGet(() -> UUID.randomUUID().toString());

        request.getHeaders().addIfAbsent(HEADER_KORRELASJONSID, korrelasjonsid);
    }

    String getBaggageField() {
        return Baggage.current().getEntryValue(HEADER_KORRELASJONSID);
    }

    protected void addClientId(HttpRequest request) {
        if (appName != null) {
            request.getHeaders().add(HEADER_KLIENTID, appName);
            request.getHeaders().add(HttpHeaders.USER_AGENT, appName);
        }
    }

    protected void addMessageId(HttpRequest request) {
        request.getHeaders().add(HEADER_MELDINGSID, UUID.randomUUID().toString());
    }
}
