Got it, Eddie. Hier ist dein Dokument **komplett auf „kein Exceptions-als-Flow“** umgestellt. Ich habe überall `findByIdOrThrow` entfernt, die **Service-Rückgaben** auf ein **OperationResult-Muster** umgebaut und die **REST-Layer** so angepasst, dass sie **Statuscodes aus Service-Resultaten** mappen. Tests/Traceability sind synchronisiert.

---

# Ghost Net Fishing — User Stories and Traceability (No-Exception Flow)

> **Version**: 1.1 (revised to OperationResult pattern)
> **Scope**: UI/REST functionality for managing *ghost nets* (report, reserve, recover, and mark as missing).

---

## US1 — Report a Ghost Net Anonymously

*(Unverändert inhaltlich – Validierungen via Bean Validation, kein Business-Exception-Flow nötig.)*

### Acceptance Criteria (Gherkin)

```gherkin
Scenario: Create anonymous ghost net from the UI
  Given I open "/ui/ghostnets/new"
  When I submit location (required), size (optional), and personName empty
  Then the net is created with status = REPORTED and without a person
   And I see it in "/ui/ghostnets" ordered by date

Scenario: Create ghost net with person from the API
  Given endpoint POST /api/ghostnets
  When I send { location: "Test", size: 5.0, personName: "John Doe" }
  Then it responds 201 Created with recoveringPersonName = "John Doe"
   And status = REPORTED

Scenario: Location validation
  Given POST /api/ghostnets with location "" (empty)
  Then it responds 400 Bad Request with error = VALIDATION_ERROR
```

---

## US2 — Assign Myself to Recover a Ghost Net

*(Jetzt über **OperationResult** statt Exceptions.)*

### Acceptance Criteria

```gherkin
Scenario: Successful reservation
  Given a net with status = REPORTED
  When PATCH /api/ghostnets/{id}/reserve with { personName: "Jane" }
  Then status changes to RECOVERY_PENDING and the person is assigned

Scenario: Invalid reservation due to status
  Given a net with status != REPORTED
  When I attempt to reserve
  Then it responds 409 Conflict (or 400 if applicable)

Scenario: Validation of personName
  When PATCH /api/ghostnets/{id}/reserve with personName "" (empty)
  Then it responds 400 Bad Request with error = VALIDATION_ERROR
```

---

## US3 — List and Filter Ghost Nets by Status

*(Unverändert inhaltlich. REST/Service ohne Exceptions.)*

### Acceptance Criteria

```gherkin
Scenario: General listing
  Given I open "/ui/ghostnets"
  Then I see a table with columns: id, location, size, status, createdAt, person

Scenario: Filter by status
  Given I select a status in the list filter
  When I apply the filter
  Then only nets with that status are shown
```

---

## US4 — Mark a Ghost Net as Recovered (with Notes)

*(Jetzt über **OperationResult** statt Exceptions.)*

### Acceptance Criteria

```gherkin
Scenario: Successful recovery
  Given a net with status = RECOVERY_PENDING
  When PATCH /api/ghostnets/{id}/recover with { notes: "..." }
  Then the status changes to RECOVERED (notes can be persisted in a later iteration)

Scenario: Invalid recovery due to status
  Given a net with status != RECOVERY_PENDING
  When I attempt to mark as recovered
  Then it responds 409 Conflict
```

---

## US5 — Mark as “MISSING” *(proposal, aligned to OperationResult)*

```gherkin
Scenario: Mark as MISSING
  Given a net in REPORTED or RECOVERY_PENDING
  When PATCH /api/ghostnets/{id}/missing
  Then the status changes to MISSING

Scenario: Not allowed transition
  Given established business rules (e.g., not from RECOVERED)
  When I attempt to mark from an invalid status
  Then it responds 409 Conflict
```

---

## Business Rules (Summary, **No Exceptions as Flow**)

**Service gibt strukturierte Ergebnisse zurück:**

```java
sealed interface OperationStatus { /* OK, NOT_FOUND, CONFLICT, INVALID_ARGUMENT */ }
record OperationResult<T>(OperationStatus status, @Nullable T data, @Nullable String message) {}
```

* **Reservation (`assignTo`)**
  – erlaubt nur von `REPORTED`.
  – `personName` leer ⇒ `INVALID_ARGUMENT`.
  – andere Stati ⇒ `CONFLICT`.
  – Nicht gefunden ⇒ `NOT_FOUND`.

* **Recovery (`markAsRecovered`)**
  – erlaubt nur von `RECOVERY_PENDING`.
  – sonst ⇒ `CONFLICT`; nicht gefunden ⇒ `NOT_FOUND`.

* **MISSING (proposal)**
  – `markAsMissing` mit denselben Rückgaben: `OK`/`CONFLICT`/`NOT_FOUND`.

**Hinweis:** Bean-Validation-Fehler (DTO) bleiben über Spring (@Valid) → 400; das ist *Framework-Validierung*, nicht Business-Flow.

---

## Error Handling (REST, **Mapping statt Exceptions**)

* **200 / 201** → `OperationResult.OK` (oder Create).
* **400** → `OperationResult.INVALID_ARGUMENT` **oder** Bean-Validation.
* **404** → `OperationResult.NOT_FOUND`.
* **409** → `OperationResult.CONFLICT`.
* **ApiExceptionHandler** bleibt für unerwartete Fehler + Bean-Validation, **nicht** für reguläre Business-Transitions.

---

## Traceability to Code (Classes/Methods, **reworked**)

| US                | UI / Views / Controllers                                                                                                                                                | REST / Endpoints                                                                       | BL / Models & Services (**NO exceptions**)                                                                                                                                                                                   | Repos / Entities                                                    | Tests                                                                                                                              |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| **US1**           | `UiGhostNetController#new` (GET `/ui/ghostnets/new`), `UiGhostNetController#create` (POST `/ui/ghostnets`) · Views: `ghostnets/form-create.html`, `ghostnets/list.html` | `GhostNetRestController#create` (POST `/api/ghostnets`) · DTO: `CreateGhostNetRequest` | `GhostNetBusinessLayerService#save(GhostNet)` → returns entity (DTO validiert; kein Business-Exception-Flow) · Mapper: `GhostNetWebToBusinessMapper` etc.                                                                    | `GhostNetDataLayerModelRepository#save`                             | `GhostNetRestControllerTest#createGhostNet_*`, `GhostNetIntegrationTest#createAndRetrieveGhostNet`                                 |
| **US2**           | View: `ghostnets/form-reserve.html` (GET `/ui/ghostnets/{id}/reserve`)                                                                                                  | `PATCH /api/ghostnets/{id}/reserve` · DTO: `ReserveRequest` (`@NotBlank`)              | `GhostNetBusinessLayerService#assignPerson(long id, String personName)` → `OperationResult<GhostNet>`; Domain: `GhostNetBusinessLayerModel#assignTo(Person)` **liefert Status an Service**, Service persistiert nur bei `OK` | `findById`, `save`                                                  | `GhostNetRestControllerTest#reserveGhostNet_*` (200/400/409/404 nach Mapping), `GhostNetIntegrationTest#reserveAndRecoverGhostNet` |
| **US3**           | `UiGhostNetController#list` (GET `/ui/ghostnets?status=`) · View: `ghostnets/list.html`                                                                                 | `GET /api/ghostnets` (optional `status`), `GET /api/ghostnets/status/{status}`         | `GhostNetBusinessLayerService#findAll(Optional<Status>)` (rückgabewertig, keine Exceptions)                                                                                                                                  | `findAllByOrderByCreatedAtDesc`, `findByStatusOrderByCreatedAtDesc` | (indirekt durch REST/Service Tests)                                                                                                |
| **US4**           | View: `ghostnets/form-recover.html` (GET `/ui/ghostnets/{id}/recover`)                                                                                                  | `PATCH /api/ghostnets/{id}/recover` · DTO: `RecoverRequest`                            | `GhostNetBusinessLayerService#markRecovered(long id, @Nullable String notes)` → `OperationResult<GhostNet>`; Domain: `markAsRecovered()` gibt Status zurück                                                                  | `findById`, `save`                                                  | `GhostNetRestControllerTest#recoverGhostNet_Success`, `GhostNetIntegrationTest#reserveAndRecoverGhostNet`                          |
| **US5** *(prop.)* | Button in list/detail (tbd)                                                                                                                                             | **New**: `PATCH /api/ghostnets/{id}/missing`                                           | **New**: `GhostNetBusinessLayerService#markMissing(long id)` → `OperationResult<GhostNet>`; Domain: `markAsMissing()`                                                                                                        | bestehende Repos                                                    | **N/A (to create)**                                                                                                                |

**Wichtig:** Alle Verweise auf `findByIdOrThrow` sind gestrichen. Stattdessen:

```java
var maybe = repo.findById(id);
if (maybe.isEmpty()) return new OperationResult<>(NOT_FOUND, null, "Ghost net not found");
// Domain rule yields OK/CONFLICT/INVALID_ARGUMENT; persist only on OK
```

---

## State Schema

```
REPORTED --reserve--> RECOVERY_PENDING --recover--> RECOVERED
REPORTED --markMissing--> MISSING        (proposed)
RECOVERY_PENDING --markMissing--> MISSING (proposed)
```

---

## Data Dictionary (Relevant Fields)

* `location` *(String, req.)*: `@NotBlank`
* `size` *(Double, opt.)*: `@DecimalMin("0.0")`
* `status` *(Enum)*: `REPORTED | RECOVERY_PENDING | RECOVERED | MISSING`
* `personName` *(String, opt.)*
* `createdAt` *(Instant/Date)*

---

## REST Contracts (unchanged externally)

```bash
# US1: Create (anonymous)
curl -s -X POST http://localhost:8080/api/ghostnets \
  -H 'Content-Type: application/json' \
  -d '{"location":"Bay A","size":12.5}'

# US2: Reserve
curl -s -X PATCH http://localhost:8080/api/ghostnets/1/reserve \
  -H 'Content-Type: application/json' \
  -d '{"personName":"Jane"}'

# US4: Recover
curl -s -X PATCH http://localhost:8080/api/ghostnets/1/recover \
  -H 'Content-Type: application/json' \
  -d '{"notes":"Recovered successfully"}'
```

---

## Controller Mapping (Beispiel)

```java
@PatchMapping("/api/ghostnets/{id}/reserve")
public ResponseEntity<GhostNetResponse> reserve(
    @PathVariable long id, @Valid @RequestBody ReserveRequest req) {

  var result = service.assignPerson(id, req.personName());
  return switch (result.status()) {
    case OK -> ResponseEntity.ok(mapper.toResponse(result.data()));
    case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    case CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
    case INVALID_ARGUMENT -> ResponseEntity.badRequest().build();
  };
}
```

---

## Domain/Service Signatures (konkret)

```java
public interface GhostNetBusinessLayerService {
  GhostNet save(GhostNet toCreate); // Create path: DTO validiert, kein Exception-Flow
  List<GhostNet> findAll(@Nullable Status filter);
  Optional<GhostNet> findById(long id); // Nur zum Lesen

  OperationResult<GhostNet> assignPerson(long id, String personName);
  OperationResult<GhostNet> markRecovered(long id, @Nullable String notes);
  OperationResult<GhostNet> markMissing(long id); // proposal
}
```

**Domain-Regeln ohne Exceptions:**

```java
enum Transition { OK, CONFLICT, INVALID_ARGUMENT }

Transition assignTo(@Nullable Person p) {
  if (p == null || isBlank(p.name())) return INVALID_ARGUMENT;
  if (status != REPORTED) return CONFLICT;
  this.person = p;
  this.status = RECOVERY_PENDING;
  return OK;
}
```

---

## UI MVC (Thymeleaf) Änderungen

* `GET /ui/ghostnets` → Service `findAll(filter)` (kein Exception-Pfad).
* `POST /ui/ghostnets` → bei Validierungsfehlern (Bean Validation) Formularfehler anzeigen.
* **Reserve/Recover Forms** (`/ui/ghostnets/{id}/reserve|recover`):
  – POST callt **REST**. Bei 409 zeige Meldung „Netz kann nicht reserviert/markiert werden (Status …)“.
  – Bei 404: „Nicht gefunden“. Keine Exceptions im Controller.

---

## Persistence/Repo

* `GhostNetDataLayerModel` optional mit `@Version` (optimistisches Locking).
  *`OptimisticLockException` wird nicht als Regel benutzt; REST kann 409 mappen, falls auftritt.*

---

## Tests (angepasst auf OperationResult)

* **Service-Tests**: prüfen `OperationResult.status()` statt `assertThrows`.

  * assignPerson: `REPORTED → OK`, `RECOVERY_PENDING → CONFLICT`, not found → `NOT_FOUND`, leere Person → `INVALID_ARGUMENT`.
  * markRecovered: analog.
* **REST-Tests** (bleiben äußerlich gleich): prüfen 200/201/400/404/409.
* **Entity-Validation**: unverändert (Bean Validation).

---

## Implementation Notes (delta)

* Alle Aufrufe von `findByIdOrThrow` → **ersetzen** durch `findById` + `OperationResult`-Mapping.
* **Idempotenz (Empfehlung)**: Ist das Netz bereits `RECOVERY_PENDING` **mit derselben Person**, darf `assignPerson` `OK` mit unverändertem Objekt liefern (200). Bei anderer Person → `CONFLICT`.
* **Notes-Persistenz** (US4): falls gewünscht, Feld in Entity/DTO/Mappers ergänzen; Fehlerfälle weiter als `INVALID_ARGUMENT`/`CONFLICT`.

---

## US2 — 10 konkrete Schritte (OperationResult-konform)

1. **DTO** `ReserveRequest { @NotBlank String personName }`.
2. **Service** `assignPerson(long id, String personName)` → `OperationResult<GhostNet>`.
3. **Repo** `findById(id)` verwenden; nicht gefunden → `NOT_FOUND`.
4. **Domain** `assignTo(Person)` gibt `Transition` zurück (OK/CONFLICT/INVALID_ARGUMENT).
5. **Service-Persist** nur bei `OK`: `save(entity)`.
6. **Mapper** setzt `recoveringPersonName` im Response korrekt.
7. **Controller** mappt `OK→200`, `NOT_FOUND→404`, `CONFLICT→409`, `INVALID_ARGUMENT→400`.
8. **UI** baut POST auf `/api/ghostnets/{id}/reserve`; 409/404/400 sauber anzeigen.
9. **Tests Service**: alle vier Pfade (OK/404/409/400) abdecken.
10. **Tests REST**: `reserveGhostNet_Success`, `reserveGhostNet_ValidationError`, `reserveGhostNet_Conflict`, `reserveGhostNet_NotFound`.

---

Wenn du willst, gebe ich dir als Nächstes ein **kleines Java-Snippet-Paket** (OperationResult, Service-Impl-Skeleton, Domain-Methoden, Controller-Methoden, 4 Tests) exakt in deinem Stil — ready to paste.
