# Aurora Spring Boot MVC Starter

A Spring Boot starter for MVC related functionality.
This starter is has a dependency on the [base-starter](https://github.com/Skatteetaten/aurora-spring-boot-base-starter).

## How to use
Include the starter as a dependency

```xml
<dependency>
  <groupId>no.skatteetaten.aurora.springboot</groupId>
  <artifactId>aurora-spring-boot-mvc-starter</artifactId>
  <version>${aurora.starters.version}</version>
</dependency>
```

## Features

### Register the Aurora Header MDC Filter

The starter will register the Aurora Header MDC Filter. The registration can be disabled with the property
```properties
aurora.mvc.header.filter.enabled = false
```

### Graceful Shutdown Handler for Tomcat

The starter will add a graceful shutdown handler for Tomcat (without it Tomcat may terminate ongoing requests on SIGTERM)


### The Aurora Management Interface

The starter will help you implement the requirements for the Aurora Management Interface by setting some common
configuration values.

Note that the `management.port` will be set to the value of the `MANAGEMENT_HTTP_PORT` environment variable provided
by the platform. The default is to put all actuator endpoints on a different port than the main application/api
endpoints.

```properties
info.serviceLinks.metrics={metricsHostname}/dashboard/db/openshift-project-spring-actuator-view?var-ds=openshift-{cluster}-ose&var-namespace={namespace}&var-app={name}

info.podLinks.metrics={metricsHostname}/dashboard/db/openshift-project-spring-actuator-view-instance?var-ds=openshift-{cluster}-ose&var-namespace={namespace}&var-app={name}&var-instance={podName}

management.health.status.order=DOWN, OUT_OF_SERVICE, UNKNOWN, OBSERVE, UP
management.port=${MANAGEMENT_HTTP_PORT:8081}
```

### Setting of Spring Boot Properties

The spring boot application name will be set from the environment variables APP_NAME and POD_NAMESPACE provided by the
platform when deploying to Aurora OpenShift.

The `flyway.out-of-order` mode will also be activated to allow migrations to be developed in different feature branches
at the same time. See the Flyway documentation for more information.

The AURORA_VERSION and IMAGE_BUILD_TIME variables are included in spring boots actuator output since we use them in a central
management overview dashboard.

```properties
spring.application.name=${APP_NAME:my}-${POD_NAMESPACE:app}
spring.jackson.date-format=com.fasterxml.jackson.databind.util.ISO8601DateFormat
flyway.out-of-order=true
info.auroraVersion= ${AURORA_VERSION:local-dev}
info.imageBuildTime=${IMAGE_BUILD_TIME:}
```
