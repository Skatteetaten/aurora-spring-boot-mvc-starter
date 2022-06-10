package no.skatteetaten.aurora.mvc.config;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.sleuth.instrument.web.HttpServerRequestParser;
import org.springframework.cloud.sleuth.zipkin2.ZipkinRestTemplateProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import brave.http.HttpRequestParser;
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer;
import no.skatteetaten.aurora.mvc.AuroraRequestParser;
import no.skatteetaten.aurora.mvc.AuroraSpanHandler;

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
        String fallbackKlientId = appVersion.isBlank() ? appName : String.format("%s/%s", appName, appVersion);
        String klientId = klientIdEnv.isBlank() ? fallbackKlientId : klientIdEnv;
        return new AuroraHeaderRestTemplateCustomizer(klientId);
    }

    @Bean(HttpServerRequestParser.NAME)
    @ConditionalOnProperty(prefix = "aurora.mvc.header.filter", name = "enabled", matchIfMissing = true)
    public HttpRequestParser sleuthHttpServerRequestParser() {
        return new AuroraRequestParser();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.zipkin", name = "enabled", havingValue = "false", matchIfMissing = true)
    public AuroraSpanHandler auroraSpanHandler() {
        return new AuroraSpanHandler();
    }

    @Bean
    @ConditionalOnProperty(prefix = "trace.auth", name = { "username", "password" })
    public ZipkinRestTemplateProvider zipkinWebClientBuilderProvider(
        @Value("${trace.auth.username}") String username,
        @Value("${trace.auth.password}") String password,
        @Value("${aurora.klientid:}") String klientId,
        RestTemplateBuilder builder
    ) {
        return () -> {
            RestTemplateBuilder builderWithHeaders = builder.basicAuthentication(username, password);

            if (klientId != null && klientId.contains("/")) {
                String affiliation = klientId.substring(0, klientId.indexOf("/"));
                builderWithHeaders = builderWithHeaders.defaultHeader(HEADER_ORGID, affiliation);
            }

            return builderWithHeaders.build();
        };
    }
}
