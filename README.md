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

### RestTemplate interceptor
The RestTemplate interceptor will add the `Korrelasjonsid`, `Meldingsid` and `Klientid` headers to requests sent from the RestTemplate instance.
To use this functionality enabled it using the property, as shown below. It is disabled by default.
Inject it as a normal Spring bean using the `RestTemplateBuilder`, where you can also add you own customization.

```properties
aurora.mvc.header.resttemplate.interceptor.enabled = true
```

The headers set are based on these values:
- `Korrelasjonsid`, taken from the `RequestKorrelasjon` class. If not found, it will generate a new ID.
- `Medlindsid`, will always generate a new ID.
- `Klientid`, set from the application name (using the `spring.application.name` property). The `User-Agent` header will also be set to the same value.

### Graceful Shutdown Handler for Tomcat

The starter will add a graceful shutdown handler for Tomcat (without it Tomcat may terminate ongoing requests on SIGTERM)


