# Get Current User Use Case

## Proposito

Documenta el futuro caso de uso para consultar el usuario autenticado.

## Responsabilidad en Clean Architecture

Pertenecera a `iam/domain/usecase` y permitira que presentation consulte sesion sin conocer DataStore, Room o red.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni consulta datos.

## Futuro archivo

Luego podra convertirse en `GetCurrentUserUseCase`, devolviendo el `User` actual o un estado de sesion ausente.
