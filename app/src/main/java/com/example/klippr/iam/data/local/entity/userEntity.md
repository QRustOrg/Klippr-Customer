# User Entity

## Proposito

Documenta la futura representacion local del usuario autenticado.

## Responsabilidad en Clean Architecture

Pertenecera a `iam/data/local/entity` como modelo persistible de Room, separado del modelo puro de dominio.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni anotaciones Room.

## Futuro archivo

Luego podra convertirse en `UserEntity`, una `Entity` usada para cachear datos de usuario y sesion.
