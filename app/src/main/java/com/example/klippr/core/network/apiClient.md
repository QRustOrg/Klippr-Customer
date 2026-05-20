# Api Client

## Proposito

Documenta el futuro cliente HTTP central de Klippr.

## Responsabilidad en Clean Architecture

Pertenecera a `core/network` como componente transversal para configurar Retrofit, OkHttp o el cliente HTTP elegido.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni crea clientes de red.

## Futuro archivo

Luego podra convertirse en `ApiClient` y exponer servicios remotos compartidos por los repositorios de `data/remote`.
