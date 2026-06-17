# Klippr — User Stories & Spike Stories

> Documento organizado por tipo de usuario:
> 🟦 **CONSUMER** (Usuario) · 🟧 **BUSINESS** (Empresa) · ⬛ **TECHNICAL** (Developer)

---

# 🟦 CONSUMER — User Stories (Usuario)

## EP01 — Landing / Exploración

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-01    | Usuario | Alta     | EP01  |

**Title:** Explorar descuentos

**Description:** Como usuario, quiere visualizar descuentos disponibles para encontrar promociones.

- Dado que el usuario accede a la aplicación,
- Cuando visualiza la lista de descuentos,
- Entonces observa promociones disponibles.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-02    | Usuario | Alta     | EP01  |

**Title:** Filtrar descuentos

**Description:** Como usuario, quiere filtrar descuentos por categoría o ubicación.

- Dado que existen filtros disponibles,
- Cuando el usuario selecciona un criterio,
- Entonces se muestran descuentos filtrados.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-03    | Usuario | Alta     | EP01  |

**Title:** Ver detalle de promoción

**Description:** Como usuario, quiere ver condiciones de una promoción.

- Dado que selecciona una promoción,
- Cuando accede al detalle,
- Entonces visualiza condiciones y vigencia.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-21    | Usuario | Media    | EP01  |

**Title:** Buscar promociones

**Description:** Como usuario, quiere buscar promociones por nombre.

- Dado un término de búsqueda,
- Cuando el usuario lo ingresa,
- Entonces el sistema muestra resultados relacionados.

---

## EP02 — Gestión de Descuentos (Usuario)

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-04    | Usuario | Alta     | EP02  |

**Title:** Generar código QR

**Description:** Como usuario, quiere generar un código QR único.

- Dado que selecciona un descuento,
- Cuando genera el código,
- Entonces el sistema crea un QR único.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-05    | Usuario | Media    | EP02  |

**Title:** Ver códigos generados

**Description:** Como usuario, quiere visualizar sus códigos.

- Dado que existen códigos generados,
- Cuando accede a la sección,
- Entonces visualiza sus códigos.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-06    | Usuario | Media    | EP02  |

**Title:** Ver historial

**Description:** Como usuario, quiere ver historial de descuentos usados.

- Dado que existen descuentos usados,
- Cuando accede al historial,
- Entonces visualiza registros.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-20    | Usuario | Media    | EP02  |

**Title:** Guardar promociones

**Description:** Como usuario, quiere guardar promociones para revisarlas después.

- Dado una promoción disponible,
- Cuando el usuario la guarda,
- Entonces el sistema la registra en favoritos.

---

## EP05 — Módulo Social

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-13    | Usuario | Media    | EP05  |

**Title:** Publicar reseña

**Description:** Como usuario, quiere compartir experiencia.

- Dado una promoción usada,
- Cuando publica reseña,
- Entonces se registra.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-14    | Usuario | Media    | EP05  |

**Title:** Calificar promoción

**Description:** Como usuario, quiere calificar. *(Bundled: el rating va dentro del flujo de publicar reseña de US-13, no es una pantalla/acción separada — decisión de producto confirmada.)*

- Dado una promoción,
- Cuando califica (como parte de publicar su reseña, US-13),
- Entonces se guarda puntuación junto con la reseña.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-15    | Usuario | Baja     | EP05  |

**Title:** Comentar

**Description:** Como usuario, quiere comentar.

- Dado una publicación,
- Cuando comenta,
- Entonces se registra.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-16    | Usuario | Baja     | EP05  |

**Title:** Reaccionar

**Description:** Como usuario, quiere reaccionar.

- Dado una publicación,
- Cuando reacciona,
- Entonces se registra.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-24    | Usuario | Media    | EP05  |

**Title:** Compartir promoción

**Description:** Como usuario, quiere compartir promociones para recomendarlas a otros.

- Dado una promoción disponible,
- Cuando el usuario la comparte,
- Entonces el sistema genera un medio para compartirla.

---

## EP06 — Autenticación

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-17    | Usuario | Media    | EP06  |

**Title:** Registro

**Description:** Como usuario, quiere registrarse.

- Dado datos válidos,
- Cuando se registra,
- Entonces crea cuenta.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-18    | Usuario | Media    | EP06  |

**Title:** Login

**Description:** Como usuario, quiere iniciar sesión.

- Dado credenciales válidas,
- Cuando inicia,
- Entonces accede.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-19    | Usuario | Baja     | EP06  |

**Title:** Recuperar contraseña

**Description:** Como usuario, quiere recuperar acceso.

- Dado correo válido,
- Cuando solicita,
- Entonces recibe enlace.

---

# 🟧 BUSINESS — User Stories (Empresa)

## EP03 — Validación (Empresa)

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-07    | Empresa | Alta     | EP03  |

**Title:** Escanear QR

**Description:** Como empresa, quiere escanear QR para validar descuentos.

- Dado un código QR válido,
- Cuando la empresa lo escanea,
- Entonces el sistema valida el descuento.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-08    | Empresa | Alta     | EP03  |

**Title:** Validación manual

**Description:** Como empresa, quiere ingresar código manual.

- Dado un código válido,
- Cuando lo ingresa manualmente,
- Entonces el sistema valida.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-09    | Empresa | Alta     | EP03  |

**Title:** Bloquear código

**Description:** Como empresa, quiere evitar reutilización.

- Dado un código validado,
- Cuando se confirma uso,
- Entonces el sistema lo bloquea.

---

## EP04 — Gestión de Promociones

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-10    | Empresa | Alta     | EP04  |

**Title:** Crear promoción

**Description:** Como empresa, quiere crear promociones.

- Dado datos válidos,
- Cuando crea promoción,
- Entonces el sistema la registra.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-11    | Empresa | Media    | EP04  |

**Title:** Definir condiciones

**Description:** Como empresa, quiere establecer condiciones.

- Dado una promoción,
- Cuando define condiciones,
- Entonces se guardan.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-12    | Empresa | Media    | EP04  |

**Title:** Limitar canjes

**Description:** Como empresa, quiere limitar cantidad.

- Dado una promoción,
- Cuando define límite,
- Entonces el sistema lo respeta.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-22    | Empresa | Alta     | EP04  |

**Title:** Editar promoción

**Description:** Como empresa, quiere modificar promociones.

- Dado una promoción existente,
- Cuando edita datos,
- Entonces el sistema actualiza la información.

---

| Story ID | User    | Priority | Epic  |
|----------|---------|----------|-------|
| US-23    | Empresa | Media    | EP04  |

**Title:** Desactivar promoción

**Description:** Como empresa, quiere desactivar promociones.

- Dado una promoción activa,
- Cuando la desactiva,
- Entonces deja de estar disponible.

---

# ⬛ TECHNICAL — Developer Stories

## EP07 — Technical Stories

| Story ID | User      | Priority | Epic  |
|----------|-----------|----------|-------|
| TS-01    | Developer | Alta     | EP07  |

**Title:** API generación QR

**Description:** Como developer, quiere generar QR.

- Dado request válido,
- Cuando ejecuta,
- Entonces retorna código QR.

---

| Story ID | User      | Priority | Epic  |
|----------|-----------|----------|-------|
| TS-02    | Developer | Alta     | EP07  |

**Title:** API validación QR

**Description:** Como developer, quiere validar QR.

- Dado request válido,
- Cuando ejecuta,
- Entonces retorna validación.

---

| Story ID | User      | Priority | Epic  |
|----------|-----------|----------|-------|
| TS-03    | Developer | Alta     | EP07  |

**Title:** API historial de uso

**Description:** Como developer, quiere obtener historial.

- Dado request válido,
- Cuando ejecuta,
- Entonces retorna historial.

---

## EP08 — Spike Stories

| Story ID | User      | Priority | Epic  |
|----------|-----------|----------|-------|
| SP-01    | Developer | Media    | EP08  |

**Title:** Investigar librerías QR

**Description:** Como developer, quiere evaluar generación QR.

- Dado investigación,
- Cuando analiza opciones,
- Entonces documenta resultados.

---

| Story ID | User      | Priority | Epic  |
|----------|-----------|----------|-------|
| SP-02    | Developer | Media    | EP08  |

**Title:** Evaluar seguridad QR

**Description:** Como developer, quiere analizar seguridad.

- Dado análisis,
- Cuando evalúa riesgos,
- Entonces documenta conclusiones.

---

| Story ID | User      | Priority | Epic  |
|----------|-----------|----------|-------|
| SP-03    | Developer | Media    | EP08  |

**Title:** Evaluar escalabilidad

**Description:** Como developer, quiere analizar rendimiento del sistema.

- Dado pruebas de carga,
- Cuando analiza resultados,
- Entonces documenta conclusiones.
