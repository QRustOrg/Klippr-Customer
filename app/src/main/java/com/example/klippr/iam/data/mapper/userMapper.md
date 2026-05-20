# User Mapper

## Proposito

Documenta el futuro mapeo entre DTOs, entidades locales y modelo de dominio.

## Responsabilidad en Clean Architecture

Pertenecera a `iam/data/mapper` para evitar que `domain` conozca formatos remotos o persistibles.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni funciones de mapeo.

## Futuro archivo

Luego podra convertirse en `UserMapper`, transformando `AuthenticatedUserDto` y `UserEntity` hacia `User`.
