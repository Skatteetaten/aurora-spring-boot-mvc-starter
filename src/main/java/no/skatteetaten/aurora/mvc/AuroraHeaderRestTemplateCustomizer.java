package no.skatteetaten.aurora.mvc;

import static no.skatteetaten.aurora.mvc.AuroraRequestParser.KLIENTID_FIELD;
import static no.skatteetaten.aurora.mvc.AuroraRequestParser.KORRELASJONSID_FIELD;
import static no.skatteetaten.aurora.mvc.AuroraRequestParser.MELDINGID_FIELD;

import java.util.UUID;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestTemplate;

import brave.baggage.BaggageField;

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

    private void addCorrelationId(HttpRequest request) {
        request.getHeaders().addIfAbsent(KORRELASJONSID_FIELD, getKorrelasjonsid());
    }

    private String getKorrelasjonsid() {
        BaggageField field = BaggageField.getByName(KORRELASJONSID_FIELD);
        if (field == null) {
            return UUID.randomUUID().toString();
        }
        return field.getValue();
    }

    private void addClientId(HttpRequest request) {
        if (appName != null) {
            request.getHeaders().add(KLIENTID_FIELD, appName);
            request.getHeaders().add(HttpHeaders.USER_AGENT, appName);
        }
    }

    private void addMessageId(HttpRequest request) {
        request.getHeaders().add(MELDINGID_FIELD, UUID.randomUUID().toString());
    }
}
