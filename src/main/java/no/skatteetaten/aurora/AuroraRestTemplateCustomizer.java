package no.skatteetaten.aurora;

import java.util.UUID;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter;
import no.skatteetaten.aurora.filter.logging.RequestKorrelasjon;

public class AuroraRestTemplateCustomizer implements RestTemplateCustomizer {
    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String id = RequestKorrelasjon.getId();
            if(id == null || id.isEmpty()) {
                id = UUID.randomUUID().toString();
                RequestKorrelasjon.setId(id);
            }

            request.getHeaders().add(AuroraHeaderFilter.KORRELASJONS_ID, id);
            return execution.execute(request, body);
        });
    }
}
