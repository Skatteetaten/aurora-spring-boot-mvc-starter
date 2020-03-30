package no.skatteetaten.aurora.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter;

@Configuration
public class MvcStarterApplicationConfig {

    @Bean
    @ConditionalOnProperty(prefix = "aurora.starter.headerfilter", name = "enabled", matchIfMissing = true)
    public FilterRegistrationBean auroraHeaderFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.addUrlPatterns("/*");
        registration.setFilter(new AuroraHeaderFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
