package no.skatteetaten.aurora;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestTemplate;

import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter;
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon;

public class AuroraRestTemplateCustomizer implements RestTemplateCustomizer {

    public static final String KLIENT_ID = "Klientid";
    public static final String MELDINGS_ID = "Meldingsid";

    @Value("${spring.application.name:}")
    private String name;

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            addCorrelationId(request);
            addClientId(request);
            addMessageId(request);
            return execution.execute(request, body);
        });
    }

    private void addCorrelationId(HttpRequest request) {
        String id = RequestKorrelasjon.getId();
        if(id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            RequestKorrelasjon.setId(id);
        }
        request.getHeaders().add(AuroraHeaderFilter.KORRELASJONS_ID, id);
    }

    private void addClientId(HttpRequest request) {
        if(name != null) {
            request.getHeaders().add(KLIENT_ID, name);
        }
    }

    private void addMessageId(HttpRequest request) {
        request.getHeaders().add(MELDINGS_ID, UUID.randomUUID().toString());
    }
}
