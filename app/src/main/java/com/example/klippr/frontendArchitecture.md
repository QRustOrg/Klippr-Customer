# Klippr Android Frontend Architecture

## Proposito

Este documento describe la organizacion inicial del frontend Android de Klippr por bounded context/feature usando Clean Architecture.

## Bounded contexts

- `core`
- `iam`
- `profile`
- `promotions`
- `redemption`
- `favorites`
- `community`
- `settings`
- `analytics`
- `navigation`

## Capas por feature

- `domain`: modelos puros de negocio, repositorios abstractos y casos de uso.
- `data`: persistencia local con Room, DTOs, APIs remotas, mappers y repositorios concretos.
- `presentation`: pantallas, ViewModels y estados de UI.

## Reglas de ubicacion

- Room ira en `data/local`.
- Retrofit o el cliente HTTP ira en `data/remote`.
- Los modelos puros de negocio iran en `domain/model`.
- Las pantallas y estados iran en `presentation`.
- `core` solo debe contener componentes compartidos y transversales.

## Sin implementacion Kotlin

Este source tree todavia no contiene implementacion Kotlin real. Los archivos actuales son placeholders documentales en Markdown para guiar la implementacion posterior.
