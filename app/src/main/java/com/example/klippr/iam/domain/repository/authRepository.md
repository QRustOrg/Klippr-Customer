# Auth Repository

## Proposito

Documenta el contrato de autenticacion del bounded context IAM.

## Responsabilidad en Clean Architecture

Pertenecera a `iam/domain/repository` como abstraccion que los casos de uso consumiran sin conocer la fuente de datos.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni interfaces activas.

## Futuro archivo

Luego podra convertirse en `AuthRepository`, con operaciones de inicio de sesion, registro y obtencion del usuario actual.
