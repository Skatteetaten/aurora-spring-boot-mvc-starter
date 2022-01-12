package no.skatteetaten.aurora.mvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.instrument.web.HttpServerRequestParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import brave.http.HttpRequestParser;
import no.skatteetaten.aurora.mvc.AuroraHeaderRestTemplateCustomizer;
import no.skatteetaten.aurora.mvc.AuroraRequestParser;
import no.skatteetaten.aurora.mvc.AuroraSpanHandler;

@EnableConfigurationProperties(MvcStarterProperties.class)
@Configuration
public class MvcStarterApplicationConfig {

    @Bean
    @ConditionalOnProperty(prefix = "aurora.mvc.header.resttemplate.interceptor", name = "enabled")
    public AuroraHeaderRestTemplateCustomizer auroraRestTemplateCustomizer(
        @Value("${spring.application.name:}") String appName
    ) {
        return new AuroraHeaderRestTemplateCustomizer(appName);
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

}
