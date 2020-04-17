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


