package no.skatteetaten.aurora.mvc.config;

import static org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties.TRACING_FILTER_ORDER;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.otel.OtelExporterProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import no.skatteetaten.aurora.mvc.AuroraFilter;
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer;
import no.skatteetaten.aurora.mvc.AuroraSpanProcessor;

@EnableConfigurationProperties(MvcStarterProperties.class)
@Configuration
public class MvcStarterApplicationConfig {

    public static final String HEADER_ORGID = "X-Scope-OrgID";

    @Bean
    @ConditionalOnProperty(prefix = "aurora.mvc.header.resttemplate.interceptor", name = "enabled")
    public AuroraHeaderRestTemplateCustomizer auroraRestTemplateCustomizer(
        @Value("${spring.application.name:}") String appName,
        @Value("${app.version:}") String appVersion,
        @Value("${aurora.klientid:}") String klientIdEnv
    ) {
        String fallbackKlientId = appVersion.isEmpty() ? appName : String.format("%s/%s", appName, appVersion);
        String klientId = klientIdEnv.isEmpty() ? fallbackKlientId : klientIdEnv;
        return new AuroraHeaderRestTemplateCustomizer(klientId);
    }

    @Bean
    @ConditionalOnProperty(prefix = "aurora.mvc.header.filter", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean<AuroraFilter> auroraFilter(Tracer tracer) {
        FilterRegistrationBean<AuroraFilter> filterRegistration =
            new FilterRegistrationBean<>(new AuroraFilter(tracer));
        filterRegistration.setOrder(TRACING_FILTER_ORDER + 5);
        filterRegistration.setDispatcherTypes(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD,
            DispatcherType.INCLUDE, DispatcherType.REQUEST);
        return filterRegistration;
    }

    @Bean
    public AuroraSpanProcessor auroraSpanProcessor(
        @Value("${openshift.cluster:}") String cluster,
        @Value("${pod.name:}") String podName,
        @Value("${aurora.klientid:}") String klientid,
        // Using namespace to set environment to match what is implemented in splunk
        @Value("${pod.namespace:}") String environment
    ) {
        return new AuroraSpanProcessor(cluster, podName, klientid, environment);
    }

    // Taken from OtlpExporterConfiguration, adds custom OrgID header with affiliation and Authorization header
    @Bean
    @ConditionalOnClass(OtlpGrpcSpanExporter.class)
    @ConditionalOnProperty(value = "spring.sleuth.otel.exporter.otlp.enabled", matchIfMissing = true)
    public OtlpGrpcSpanExporter otelOtlpGrpcSpanExporter(
        @Value("${aurora.klientid:}") String klientid,
        @Value("${trace.auth.username:}") String username,
        @Value("${trace.auth.password:}") String password,
        OtelExporterProperties properties
    ) {
        OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();
        String endpoint = properties.getOtlp().getEndpoint();
        if (StringUtils.hasText(endpoint)) {
            builder.setEndpoint(endpoint);
        }
        Long timeout = properties.getOtlp().getTimeout();
        if (timeout != null) {
            builder.setTimeout(timeout, TimeUnit.MILLISECONDS);
        }
        Map<String, String> headers = properties.getOtlp().getHeaders();
        if (!headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        addOrgIdHeader(klientid, builder);
        addAuthHeader(username, password, builder);

        return builder.build();
    }

    private static void addOrgIdHeader(String klientid, OtlpGrpcSpanExporterBuilder builder) {
        if (klientid != null && klientid.contains("/")) {
            String affiliation = klientid.substring(0, klientid.indexOf("/"));
            builder.addHeader(HEADER_ORGID, affiliation);
        }
    }

    private static void addAuthHeader(String username, String password, OtlpGrpcSpanExporterBuilder builder) {
        if (!username.isEmpty() && !password.isEmpty()) {
            builder.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Basic " + HttpHeaders.encodeBasicAuth(username, password, null)
            );
        }
    }
}
