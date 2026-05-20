# Auth Interceptor

## Proposito

Documenta el futuro interceptor de autenticacion para peticiones HTTP.

## Responsabilidad en Clean Architecture

Pertenecera a la infraestructura compartida de red para adjuntar credenciales de sesion sin acoplar las features a detalles de transporte.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni manipula cabeceras.

## Futuro archivo

Luego podra convertirse en `AuthInterceptor`, encargado de adjuntar el JWT a las peticiones autorizadas.
