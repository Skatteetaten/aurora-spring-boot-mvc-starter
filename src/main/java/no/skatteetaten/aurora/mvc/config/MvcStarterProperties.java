package no.skatteetaten.aurora.mvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aurora.mvc.header")
public class MvcStarterProperties {
    private AuroraPropsFilter filter;
    private AuroraPropsResttemplate resttemplate;

    public AuroraPropsFilter getFilter() {
        return filter;
    }

    public void setFilter(AuroraPropsFilter filter) {
        this.filter = filter;
    }

    public AuroraPropsResttemplate getResttemplate() {
        return resttemplate;
    }

    public void setResttemplate(AuroraPropsResttemplate resttemplate) {
        this.resttemplate = resttemplate;
    }

    public static class AuroraPropsFilter {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class AuroraPropsResttemplate {
        private AuroraPropsInterceptor interceptor;

        public AuroraPropsInterceptor getInterceptor() {
            return interceptor;
        }

        public void setInterceptor(
            AuroraPropsInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public static class AuroraPropsInterceptor {
            private boolean enabled;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
}
