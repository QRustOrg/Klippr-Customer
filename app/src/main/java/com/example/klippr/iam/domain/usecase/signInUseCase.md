# Sign In Use Case

## Proposito

Documenta el futuro caso de uso para iniciar sesion.

## Responsabilidad en Clean Architecture

Pertenecera a `iam/domain/usecase` y coordinara la autenticacion sin exponer detalles de red, base local o UI.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni logica de login.

## Futuro archivo

Luego podra convertirse en `SignInUseCase`, consumiendo `AuthRepository` y devolviendo un resultado de autenticacion.
