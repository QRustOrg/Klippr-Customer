# Klippr Database

## Proposito

Documenta la futura base de datos local principal del proyecto Klippr.

## Responsabilidad en Clean Architecture

Pertenecera a `core/database` como infraestructura compartida para centralizar la configuracion Room usada por los bounded contexts.

## Sin implementacion Kotlin

Este archivo no contiene implementacion Kotlin real ni declara una base Room todavia.

## Futuro archivo

Luego podra convertirse en `KlipprDatabase`, una clase `RoomDatabase` que registre `Entity`, `Dao` y migraciones compartidas.
