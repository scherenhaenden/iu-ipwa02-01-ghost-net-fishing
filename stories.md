# Ghost Net Fishing — User Stories y Trazabilidad

> **Versión**: 1.0
> **Ámbito**: funcionalidad UI/REST para gestión de *ghost nets* (reportar, reservar, recuperar y marcar como missing).

---

## US1 — Reportar un ghost net de forma anónima

**Como** reportante
**quiero** registrar un ghost net con ubicación (y tamaño opcional), **incluso sin dar mi nombre**,
**para** que quede en el sistema para su futura recuperación.

### Criterios de aceptación (Gherkin)

```gherkin
Scenario: Crear ghost net anónimo desde la UI
  Given abro "/ui/ghostnets/new"
  When envío location (obligatorio), size (opcional) y personName vacío
  Then el net se crea con status = REPORTED y sin persona
   And lo veo en "/ui/ghostnets" ordenado por fecha

Scenario: Crear ghost net con persona desde la API
  Given endpoint POST /api/ghostnets
  When envío { location: "Test", size: 5.0, personName: "John Doe" }
  Then responde 201 Created con recoveringPersonName = "John Doe"
   And status = REPORTED

Scenario: Validación de location
  Given POST /api/ghostnets con location "" (vacío)
  Then responde 400 Bad Request con error = VALIDATION_ERROR
```

---

## US2 — Asignarme para recuperar un ghost net

**Como** voluntario
**quiero** asignarme a un ghost net reportado,
**para** indicar que me encargo de su recuperación.

### Criterios de aceptación

```gherkin
Scenario: Reserva exitosa
  Given un net con status = REPORTED
  When PATCH /api/ghostnets/{id}/reserve con { personName: "Jane" }
  Then status cambia a RECOVERY_PENDING y queda asignada la persona

Scenario: Reserva inválida por estado
  Given un net con status != REPORTED
  When intento reservar
  Then responde 409 Conflict (o 400 si aplica)

Scenario: Validación de personName
  When PATCH /api/ghostnets/{id}/reserve con personName "" (vacío)
  Then responde 400 Bad Request con error = VALIDATION_ERROR
```

---

## US3 — Listar y filtrar ghost nets por estado

**Como** usuario
**quiero** ver una lista de ghost nets y filtrarlos por estado,
**para** entender rápidamente qué está reportado, pendiente o recuperado.

### Criterios de aceptación

```gherkin
Scenario: Listado general
  Given abro "/ui/ghostnets"
  Then veo tabla con columnas: id, location, size, status, createdAt, person

Scenario: Filtrar por estado
  Given selecciono un estado en el filtro de la lista
  When aplico el filtro
  Then solo se muestran nets con ese estado
```

---

## US4 — Marcar un ghost net como recuperado (con notas)

**Como** voluntario asignado
**quiero** marcar el ghost net como **RECOVERED** y opcionalmente adjuntar **notas**,
**para** finalizar la intervención y dejar registro.

### Criterios de aceptación

```gherkin
Scenario: Recuperación exitosa
  Given un net con status = RECOVERY_PENDING
  When PATCH /api/ghostnets/{id}/recover con { notes: "..." }
  Then el status cambia a RECOVERED (las notas pueden persistirse en una iteración posterior)

Scenario: Recuperación inválida por estado
  Given un net con status != RECOVERY_PENDING
  When intento marcar como recuperado
  Then responde 409 Conflict
```

---

## US5 — Marcar un ghost net como “MISSING” (no localizado) *(propuesta)*

**Como** operador
**quiero** marcar un reportado como **MISSING** cuando no se encuentre,
**para** mantener el estado real y evitar falsas recuperaciones.

### Criterios de aceptación

```gherkin
Scenario: Marcar como MISSING
  Given un net en REPORTED o RECOVERY_PENDING
  When PATCH /api/ghostnets/{id}/missing
  Then el estado cambia a MISSING

Scenario: Transición no permitida
  Given reglas de negocio establecidas (p.ej., no desde RECOVERED)
  When intento marcar desde un estado inválido
  Then responde 409 Conflict
```

> **Nota**: US5 aún no está implementada en el código actual; se deja trazabilidad como *propuesta*.

---

## Reglas de negocio (resumen)

* **Reserva** (`assignTo`): solo permitida desde `REPORTED`. Si persona es `null` ⇒ `IllegalArgumentException`. Si estado inválido ⇒ `IllegalStateException`.
* **Recuperación** (`markAsRecovered`): solo permitida desde `RECOVERY_PENDING`; de lo contrario ⇒ `IllegalStateException`.
* **MISSING**: por implementar; se recomienda `markAsMissing()` con validaciones equivalentes.

## Manejo de errores (REST)

* 404 → `ResourceNotFoundException` (mapeado por `ApiExceptionHandler`).
* 400 → validaciones (`MethodArgumentNotValidException`) y `IllegalArgumentException`.
* 409 → `IllegalStateException` (transiciones inválidas).

---

## Trazabilidad a código (clases/métodos)

| US                | UI / Vistas / Controladores                                                                                                                                              | REST / Endpoints                                                                                                | BL / Modelos & Servicios                                                                                                                                                                   | Repos / Entidades                                                                                                      | Tests                                                                                                     |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| **US1**           | `UiGhostNetController#new` (GET `/ui/ghostnets/new`), `UiGhostNetController#create` (POST `/ui/ghostnets`) · Vistas: `ghostnets/form-create.html`, `ghostnets/list.html` | `GhostNetRestController#create` (POST `/api/ghostnets`) · DTO: `CreateGhostNetRequest`                          | `GhostNetBusinessLayerModel` (estado `REPORTED`), `GhostNetBusinessLayerService#save` · Mappers: `GhostNetWebToBusinessMapper`, `GhostNetBusinessLayerMapper`, `PersonBusinessLayerMapper` | `GhostNetDataLayerModelRepository#save` · Entidades: `GhostNetDataLayerModel`, `PersonDataLayerModel`                  | `GhostNetRestControllerTest#createGhostNet_*`, `GhostNetIntegrationTest#createAndRetrieveGhostNet`        |
| **US2**           | Vista: `ghostnets/form-reserve.html` (GET `/ui/ghostnets/{id}/reserve`) *(POST UI pendiente)*                                                                            | `GhostNetRestController#reserve` (PATCH `/api/ghostnets/{id}/reserve`) · DTO: `ReserveRequest`                  | `GhostNetBusinessLayerModel#assignTo(Person)` (valida estado/persona), `GhostNetBusinessLayerService#findByIdOrThrow`/`save` · `PersonWebToBusinessMapper`                                 | `GhostNetDataLayerModelRepository#findById`/`save`                                                                     | `GhostNetRestControllerTest#reserveGhostNet_*`, `GhostNetIntegrationTest#reserveAndRecoverGhostNet`       |
| **US3**           | `UiGhostNetController#list` (GET `/ui/ghostnets?status=`) · Vista: `ghostnets/list.html`                                                                                 | `GhostNetRestController#findAll` (GET `/api/ghostnets`), `#findByStatus` (GET `/api/ghostnets/status/{status}`) | `GhostNetBusinessLayerService#findAll` / `#findByStatus`                                                                                                                                   | `GhostNetDataLayerModelRepository#findAllByOrderByCreatedAtDesc`, `#findByStatus`, `#findByStatusOrderByCreatedAtDesc` | *(cubierto indirectamente por tests de REST/servicio)*                                                    |
| **US4**           | Vista: `ghostnets/form-recover.html` (GET `/ui/ghostnets/{id}/recover`) *(POST UI pendiente)*                                                                            | `GhostNetRestController#recover` (PATCH `/api/ghostnets/{id}/recover`) · DTO: `RecoverRequest`                  | `GhostNetBusinessLayerModel#markAsRecovered()` (valida estado), `GhostNetBusinessLayerService#findByIdOrThrow`/`save`                                                                      | `GhostNetDataLayerModelRepository`                                                                                     | `GhostNetRestControllerTest#recoverGhostNet_Success`, `GhostNetIntegrationTest#reserveAndRecoverGhostNet` |
| **US5** *(prop.)* | Botón en lista o detalle *(a definir)*                                                                                                                                   | **Nuevo**: PATCH `/api/ghostnets/{id}/missing`                                                                  | **Nuevo**: `GhostNetBusinessLayerModel#markAsMissing()` y validaciones                                                                                                                     | Repos existentes                                                                                                       | **N/A (a crear)**                                                                                         |

---

## Esquema de estados (actual + propuesto)

```
REPORTED --reserve--> RECOVERY_PENDING --recover--> RECOVERED
REPORTED --markMissing--> MISSING        (propuesto)
RECOVERY_PENDING --markMissing--> MISSING (propuesto)
```

---

## Diccionario de datos (campos relevantes)

* `location` *(String, req.)*: ubicación o coordenadas; validación `@NotBlank` en REST; UI valida y muestra error.
* `size` *(Double, opc.)*: ≥ 0 (`@DecimalMin("0.0")`).
* `status` *(Enum)*: `REPORTED | RECOVERY_PENDING | RECOVERED | MISSING`.
* `personName` *(String, opc.)*: si se informa al crear o reservar, se persiste vía `Person*` mappers.
* `createdAt` *(Instant/Date)*: sellado al crear; repos con ordenado descendente disponible.

---

## Ejemplos rápidos (cURL)

```bash
# US1: Crear (anónimo)
curl -s -X POST http://localhost:8080/api/ghostnets \
  -H 'Content-Type: application/json' \
  -d '{"location":"Bay A","size":12.5}'

# US2: Reservar
curl -s -X PATCH http://localhost:8080/api/ghostnets/1/reserve \
  -H 'Content-Type: application/json' \
  -d '{"personName":"Jane"}'

# US4: Recuperar
curl -s -X PATCH http://localhost:8080/api/ghostnets/1/recover \
  -H 'Content-Type: application/json' \
  -d '{"notes":"Recuperado con éxito"}'
```

---

## Notas de implementación

* **UI**: en `form-create.html` el botón está marcado como *API pendiente*; ya existe `UiGhostNetController#create` que persiste vía BL. Para completar ciclo UI de US2/US4, añadir POST de `reserve`/`recover` contra la API o directamente contra el servicio.
* **Persistencia de notas** (US4): añadir campo `notes` a `GhostNet*` (entidad + modelos + mappers + DTO `RecoverRequest`) si se decide almacenar.
* **MISSING** (US5): crear endpoint, método BL y tests análogos a `recover`.

---

**Fin del documento.**


¡Vamos! Asumiendo que la **Historia 1** es: *“Como usuario, quiero **reportar** una ghost net (ubicación, tamaño, opcionalmente nombre), **listar** las ghost nets con filtro por estado y **ver detalles** de una”*. Con lo que ya tienes, esto es lo que **falta** y **exactamente** lo que hay que hacer, partido en 10 pasos cortos:

1. **Criterios de aceptación (clavar el marco)**

   * Reporte crea una net con `REPORTED`, `createdAt` ahora, y persona opcional.
   * Lista muestra todas con filtro por estado y “anónimo” si no hay persona (tu lista ya lo pinta).
   * Detalle devuelve una por id (REST y/o vista simple).
   * Validaciones: `location` no vacío, `size ≥ 0`.
   * Errores: 400 por validación, 404 si no existe (ya tienes `ApiExceptionHandler`).

2. **Entidades JPA con validación (BD)**

   * Asegura que **las entidades de base de datos** (GhostNet/Person) lleven anotaciones de validación: `@NotBlank` en ubicación, `@PositiveOrZero` en tamaño, etc.
   * Marca relaciones (`ManyToOne` para persona) y `createdAt` como pasado/presente.
   * Objetivo: si alguien intenta guardar datos inválidos, falle.

3. **Repositorios JPA**

   * Crea repos para GhostNet y Person.
   * Añade un método de consulta por **estado** para la lista filtrada.
   * Objetivo: el servicio no toque EntityManager directo.

4. **Servicio de dominio (reglas)**

   * Implementa `save`, `findByIdOrThrow`, `findAll(Optional<status>)`.
   * Implementa reglas: al crear → `REPORTED`; si se asigna persona → `RECOVERY_PENDING`; al marcar recuperado → `RECOVERED` (con chequeos de estado válidos).
   * Lanza `ResourceNotFoundException` si no existe.

pero decidí cambiar esa idea a una mejor sin usar exceptions como flujo de programa, así que:

   4. **Servicio de dominio (reglas, sin exceptions)**

* **Métodos**: `save`, `findById` *(Optional)*, `findAll(Optional<status>)`, `assignPerson`, `markRecovered`, `deleteById`.
* **Crear (`save`)**: si `status==null` ⇒ `REPORTED`; si `createdAt==null` ⇒ `now`.
* **`assignPerson(id, person)`**: si no existe ⇒ **NOT_FOUND**; si `status==REPORTED` ⇒ set `person`, `RECOVERY_PENDING` ⇒ **OK**; otro estado ⇒ **CONFLICT**.
* **`markRecovered(id)`**: si no existe ⇒ **NOT_FOUND**; si `status==RECOVERY_PENDING` ⇒ `RECOVERED` ⇒ **OK**; otro estado ⇒ **CONFLICT**.
* **Retornos**: comandos devuelven `OperationResult { OK | NOT_FOUND | CONFLICT }`; **controllers** mapean a **200/404/409**.
* **Acceso a datos**: solo **repos** (`findAllByOrderByCreatedAtDesc`, `findByStatusOrderByCreatedAtDesc`); **sin EntityManager**.


5. **REST Controller (alineado a tus tests)**

   * Endpoints:

     * `POST /api/ghostnets` (crear con validación; devuelve 201 + JSON).
     * `GET /api/ghostnets/{id}` (detalle).
     * (Opcional en H1) `GET /api/ghostnets?status=...` (lista filtrada para futuros tests).
   * Nota: tus tests ya cubren también `PATCH /reserve` y `PATCH /recover`; si **no** forman parte de H1, déjalos para H2. Si sí lo son, impleméntalos con validación de entrada (nombre y notas).

6. **Mapper web↔negocio (ya tienes)**

   * Revisa que el mapper **a web** ponga `recoveringPersonName` a `null` si no hay persona (lo hace).
   * Revisa que el mapper **de creación** ponga `REPORTED` y `createdAt` (ya lo hace).

7. **UI MVC (Thymeleaf)**

   * Ya tienes vistas y nav. Falta:

     * Controlador MVC que:

       * GET `/ui/ghostnets` → llama al servicio con el filtro y pasa la lista.
       * GET `/ui/ghostnets/new` → muestra formulario con `ghostNetForm`.
       * POST `/ui/ghostnets` → valida, llama al servicio, redirige a la lista con mensaje.
       * (Opcional) GET detalle simple si lo quieres mostrar.
   * Asegura que la ruta del CSS cargue (tu `WebConfig` + `th:href="@{/static/styles.css}"` está bien).

8. **Limpieza de restos Java EE**

   * Ignora/elimina `beans.xml`, `context.xml`, artefactos de TomEE/JSF para evitar confusión en Spring Boot.
   * Deja **solo** Spring Boot (jar embebido) como camino de ejecución.

9. **Pruebas que faltan (mínimas y directas)**

   * **Validación de entidades**: tests que verifiquen que `location=""` y `size<0` producen violaciones.
   * **Servicio**: tests con repos simulados (o H2) para:

     * crear → `REPORTED`;
     * asignar persona → `RECOVERY_PENDING`;
     * recuperar → `RECOVERED`;
     * 404 en `findByIdOrThrow`.
   * **MVC UI** (opcional H1): test de controlador que la lista renderiza y respeta el filtro.
   * Tus tests REST ya están esbozados; completa el controller para que pasen los de H1 (POST/GET).

10. **Ejecución & checklist manual**

* Levanta la app, abre `/ui/ghostnets`.
* Reporta una net válida, verifica en la lista (estado, tamaño, createdAt, anónimo).
* Prueba filtro por estado.
* Verifica también vía REST `POST` y `GET` con `curl`/Postman que responde 201/200 y 400/404 cuando toca.
* Marca como “Hecho” cada criterio de aceptación.

---

## BD para probar hoy (rápido)

* **In-memory (H2)**: ya lo tienes configurado. Simplemente ejecuta la app y tendrás base **volátil** por sesión. Útil para pruebas inmediatas (no persiste al reiniciar).
* **SQLite (archivo pequeño y persistente)**: si prefieres guardar entre reinicios:

  1. Añade el driver SQLite y el dialecto de Hibernate Community.
  2. Crea un **perfil** (por ejemplo `sqlite`) con estos ajustes:

     * URL JDBC tipo `jdbc:sqlite:./ghostnet.db`
     * Usuario/clave vacíos
     * Dialecto SQLite
     * `ddl-auto=update`
  3. Arranca con ese perfil activo para que use el archivo `ghostnet.db` en la raíz del proyecto.

Con esto tienes una ruta de 10 pasos, clara y finita, para cerrar la Historia 1 hoy mismo. Si quieres, te digo cuál de esos pasos te conviene implementar **primero** con el menor riesgo (spoiler: 2→3→4→5→7).
