# Change Log

Alle viktige endringer i dette prosjektet vil bli dokumentert i denne filen.

Formatet i denne filen er basert på [Keep a Changelog](http://keepachangelog.com/)
og prosjektet følger [Semantic Versioning](http://semver.org/).

## [1.7.2] - 2022-11-04
- Oppdatert aurora-gradle-plugin og base-starter versjon

## [1.7.1] - 2022-09-20
- Oppdatert aurora-gradle-plugin og base-starter versjon

## [1.7.0] - 2022-08-11
- Oppdatert dependencies
- Bruker gradle i stedet for maven for å bygge starter

## [1.6.4] - 2022-06-22
- Fikset feil hvor trace data ble inkludert i zipkin requests

## [1.6.3] - 2022-06-15
- Trace tag prefix to `skatteetaten`
- Generelle trace tagger er nå satt i AuroraSpanHandler

## [1.6.1] - 2022-05-19

### Changed

- Oppgraderte til SpringBoot til versjon 2.6.8.
- Oppgraderte versjon av aurora-spring-boot-base-starter til versjon 1.3.12.

## [1.5.2] - 2022-05-04

### Changed

- Oppgraderte til SpringBoot til versjon 2.6.7.
- Oppgraderte versjon av aurora-spring-boot-base-starter til versjon 1.3.10.
- Oppgraderte versjon av spring-cloud-dependencies tilm versjon 2021.0.2

## [1.5.0] - 2022-04-05

### Added

- La til støtte for bruk av AURORA_KLIENTID miljøvariabelen i AuroraHeaderRestTemplateCustomizer.

## [1.4.7] - 2022-04-01

### Changed

- Oppgraderte til SpringBoot versjon 2.6.6.
- Oppgraderte versjon av aurora-spring-boot-base-starter versjon 1.3.9.

## [1.4.5] - 2022-03-29

### Changed

- Oppgraderte til SpringBoot versjon 2.6.5.
- Oppgraderte versjon av aurora-spring-boot-base-starter versjon 1.3.7.

### Added

- CHANGELOG.md for å dokumentere endringer.
