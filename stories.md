# Ghost Net Fishing — User Stories and Traceability

> **Version**: 1.0
> **Scope**: UI/REST functionality for managing *ghost nets* (report, reserve, recover, and mark as missing).

---

## US1 — Report a Ghost Net Anonymously

**As** a reporter
**I want** to register a ghost net with location (and optional size), **even without giving my name**,
**so that** it is recorded in the system for future recovery.

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

**As** a volunteer
**I want** to assign myself to a reported ghost net,
**so that** I can indicate that I am in charge of its recovery.

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

**As** a user
**I want** to see a list of ghost nets and filter them by status,
**so that** I can quickly understand what is reported, pending, or recovered.

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

**As** an assigned volunteer
**I want** to mark the ghost net as **RECOVERED** and optionally attach **notes**,
**so that** I can finalize the intervention and leave a record.

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

## US5 — Mark a Ghost Net as “MISSING” (Not Located) *(proposal)*

**As** an operator
**I want** to mark a reported net as **MISSING** when it is not found,
**so that** I can maintain the real status and avoid false recoveries.

### Acceptance Criteria

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

> **Note**: US5 is not yet implemented in the current code; traceability is left as a *proposal*.

---

## Business Rules (Summary)

* **Reservation** (`assignTo`): only allowed from `REPORTED`. If person is `null` ⇒ `IllegalArgumentException`. If invalid status ⇒ `IllegalStateException`.
* **Recovery** (`markAsRecovered`): only allowed from `RECOVERY_PENDING`; otherwise ⇒ `IllegalStateException`.
* **MISSING**: to be implemented; recommend `markAsMissing()` with equivalent validations.

## Error Handling (REST)

* 404 → `ResourceNotFoundException` (mapped by `ApiExceptionHandler`).
* 400 → validations (`MethodArgumentNotValidException`) and `IllegalArgumentException`.
* 409 → `IllegalStateException` (invalid transitions).

---

## Traceability to Code (Classes/Methods)

| US                | UI / Views / Controllers                                                                                                                                              | REST / Endpoints                                                                                                | BL / Models & Services                                                                                                                                                                   | Repos / Entities                                                                                                      | Tests                                                                                                     |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| **US1**           | `UiGhostNetController#new` (GET `/ui/ghostnets/new`), `UiGhostNetController#create` (POST `/ui/ghostnets`) · Views: `ghostnets/form-create.html`, `ghostnets/list.html` | `GhostNetRestController#create` (POST `/api/ghostnets`) · DTO: `CreateGhostNetRequest`                          | `GhostNetBusinessLayerModel` (status `REPORTED`), `GhostNetBusinessLayerService#save` · Mappers: `GhostNetWebToBusinessMapper`, `GhostNetBusinessLayerMapper`, `PersonBusinessLayerMapper` | `GhostNetDataLayerModelRepository#save` · Entities: `GhostNetDataLayerModel`, `PersonDataLayerModel`                  | `GhostNetRestControllerTest#createGhostNet_*`, `GhostNetIntegrationTest#createAndRetrieveGhostNet`        |
| **US2**           | View: `ghostnets/form-reserve.html` (GET `/ui/ghostnets/{id}/reserve`) *(POST UI pending)*                                                                            | `GhostNetRestController#reserve` (PATCH `/api/ghostnets/{id}/reserve`) · DTO: `ReserveRequest`                  | `GhostNetBusinessLayerModel#assignTo(Person)` (validates status/person), `GhostNetBusinessLayerService#findByIdOrThrow`/`save` · `PersonWebToBusinessMapper`                                 | `GhostNetDataLayerModelRepository#findById`/`save`                                                                     | `GhostNetRestControllerTest#reserveGhostNet_*`, `GhostNetIntegrationTest#reserveAndRecoverGhostNet`       |
| **US3**           | `UiGhostNetController#list` (GET `/ui/ghostnets?status=`) · View: `ghostnets/list.html`                                                                                 | `GhostNetRestController#findAll` (GET `/api/ghostnets`), `#findByStatus` (GET `/api/ghostnets/status/{status}`) | `GhostNetBusinessLayerService#findAll` / `#findByStatus`                                                                                                                                   | `GhostNetDataLayerModelRepository#findAllByOrderByCreatedAtDesc`, `#findByStatus`, `#findByStatusOrderByCreatedAtDesc` | *(covered indirectly by REST/service tests)*                                                    |
| **US4**           | View: `ghostnets/form-recover.html` (GET `/ui/ghostnets/{id}/recover`) *(POST UI pending)*                                                                            | `GhostNetRestController#recover` (PATCH `/api/ghostnets/{id}/recover`) · DTO: `RecoverRequest`                  | `GhostNetBusinessLayerModel#markAsRecovered()` (validates status), `GhostNetBusinessLayerService#findByIdOrThrow`/`save`                                                                      | `GhostNetDataLayerModelRepository`                                                                                     | `GhostNetRestControllerTest#recoverGhostNet_Success`, `GhostNetIntegrationTest#reserveAndRecoverGhostNet` |
| **US5** *(prop.)* | Button in list or detail *(to be defined)*                                                                                                                                   | **New**: PATCH `/api/ghostnets/{id}/missing`                                                                  | **New**: `GhostNetBusinessLayerModel#markAsMissing()` and validations                                                                                                                     | Existing repos                                                                                                       | **N/A (to create)**                                                                                         |

---

## State Schema (Current + Proposed)

```
REPORTED --reserve--> RECOVERY_PENDING --recover--> RECOVERED
REPORTED --markMissing--> MISSING        (proposed)
RECOVERY_PENDING --markMissing--> MISSING (proposed)
```

---

## Data Dictionary (Relevant Fields)

* `location` *(String, req.)*: location or coordinates; validation `@NotBlank` in REST; UI validates and shows error.
* `size` *(Double, opt.)*: ≥ 0 (`@DecimalMin("0.0")`).
* `status` *(Enum)*: `REPORTED | RECOVERY_PENDING | RECOVERED | MISSING`.
* `personName` *(String, opt.)*: if provided on create or reserve, it is persisted via `Person*` mappers.
* `createdAt` *(Instant/Date)*: stamped on create; repos with descending order available.

---

## Quick Examples (cURL)

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

## Implementation Notes

* **UI**: in `form-create.html` the button is marked as *API pending*; `UiGhostNetController#create` already exists that persists via BL. To complete the UI cycle for US2/US4, add POST for `reserve`/`recover` against the API or directly against the service.
* **Persistence of notes** (US4): add `notes` field to `GhostNet*` (entity + models + mappers + DTO `RecoverRequest`) if decided to store.
* **MISSING** (US5): create endpoint, BL method, and analogous tests to `recover`.

---


Assuming that **Story 1** is: *“As a user, I want to **report** a ghost net (location, size, optionally name), **list** the ghost nets with filter by status and **view details** of one”*. With what you already have, this is what is **missing** and **exactly** what needs to be done, broken down into 10 short steps:

1. **Acceptance Criteria (Nail the Framework)**

    * Report creates a net with `REPORTED`, `createdAt` now, and optional person.
    * List shows all with filter by status and “anonymous” if no person (your list already paints it).
    * Detail returns one by id (REST and/or simple view).
    * Validations: `location` not empty, `size ≥ 0`.
    * Errors: 400 for validation, 404 if not exists (you already have `ApiExceptionHandler`).

2. **JPA Entities with Validation (DB)**

    * Ensure that **database entities** (GhostNet/Person) carry validation annotations: `@NotBlank` on location, `@PositiveOrZero` on size, etc.
    * Mark relationships (`ManyToOne` for person) and `createdAt` as past/present.
    * Goal: if someone tries to save invalid data, it fails.

3. **JPA Repositories**

    * Create repos for GhostNet and Person.
    * Add a query method by **status** for the filtered list.
    * Goal: the service doesn't touch EntityManager directly.

4. **Domain Service (Rules)**

    * Implement `save`, `findByIdOrThrow`, `findAll(Optional<status>)`.
    * Implement rules: on create → `REPORTED`; if person is assigned → `RECOVERY_PENDING`; on mark recovered → `RECOVERED` (with valid status checks).
    * Throw `ResourceNotFoundException` if not exists.

but I decided to change that idea to a better one without using exceptions as program flow, so:

4. **Domain Service (Rules, without exceptions)**

* **Methods**: `save`, `findById` *(Optional)*, `findAll(Optional<status>)`, `assignPerson`, `markRecovered`, `deleteById`.
* **Create (`save`)**: if `status==null` ⇒ `REPORTED`; if `createdAt==null` ⇒ `now`.
* **`assignPerson(id, person)`**: if not exists ⇒ **NOT_FOUND**; if `status==REPORTED` ⇒ set `person`, `RECOVERY_PENDING` ⇒ **OK**; other status ⇒ **CONFLICT**.
* **`markRecovered(id)`**: if not exists ⇒ **NOT_FOUND**; if `status==RECOVERY_PENDING` ⇒ `RECOVERED` ⇒ **OK**; other status ⇒ **CONFLICT**.
* **Returns**: commands return `OperationResult { OK | NOT_FOUND | CONFLICT }`; **controllers** map to **200/404/409**.
* **Data Access**: only **repos** (`findAllByOrderByCreatedAtDesc`, `findByStatusOrderByCreatedAtDesc`); **no EntityManager**.


5. **REST Controller (Aligned to Your Tests)**

    * Endpoints:

        * `POST /api/ghostnets` (create with validation; returns 201 + JSON).
        * `GET /api/ghostnets/{id}` (detail).
        * (Optional in H1) `GET /api/ghostnets?status=...` (filtered list for future tests).
    * Note: your tests already cover `PATCH /reserve` and `PATCH /recover` too; if **not** part of H1, leave them for H2. If yes, implement them with input validation (name and notes).

6. **Web↔Business Mapper (You Already Have It)**

    * Review that the mapper **to web** sets `recoveringPersonName` to `null` if no person (it does).
    * Review that the **creation** mapper sets `REPORTED` and `createdAt` (it already does).

7. **UI MVC (Thymeleaf)**

    * You already have views and nav. Missing:

        * MVC Controller that:

            * GET `/ui/ghostnets` → calls the service with the filter and passes the list.
            * GET `/ui/ghostnets/new` → shows form with `ghostNetForm`.
            * POST `/ui/ghostnets` → validates, calls the service, redirects to the list with message.
            * (Optional) Simple GET detail if you want to show it.
    * Ensure the CSS path loads (your `WebConfig` + `th:href="@{/static/styles.css}"` is fine).

8. **Cleanup of Java EE Remnants**

    * Ignore/delete `beans.xml`, `context.xml`, TomEE/JSF artifacts to avoid confusion in Spring Boot.
    * Keep **only** Spring Boot (embedded jar) as the execution path.

9. **Missing Tests (Minimal and Direct)**

    * **Entity Validation**: tests that verify `location=""` and `size<0` produce violations.
    * **Service**: tests with mocked repos (or H2) for:

        * create → `REPORTED`;
        * assign person → `RECOVERY_PENDING`;
        * recover → `RECOVERED`;
        * 404 on `findByIdOrThrow`.
    * **MVC UI** (optional H1): controller test that the list renders and respects the filter.
    * Your REST tests are already sketched; complete the controller so the H1 ones pass (POST/GET).

10. **Execution & Manual Checklist**

* Start the app, open `/ui/ghostnets`.
* Report a valid net, verify in the list (status, size, createdAt, anonymous).
* Test filter by status.
* Also verify via REST `POST` and `GET` with `curl`/Postman that it responds 201/200 and 400/404 when appropriate.
* Mark each acceptance criterion as “Done”.

---

## DB for Testing Today (Quick)

* **In-memory (H2)**: you already have it configured. Simply run the app and you'll have a **volatile** database per session. Useful for immediate tests (doesn't persist on restart).
* **SQLite (small file and persistent)**: if you prefer to save between restarts:

    1. Add the SQLite driver and Hibernate Community dialect.
    2. Create a **profile** (e.g., `sqlite`) with these settings:

        * JDBC URL like `jdbc:sqlite:./ghostnet.db`
        * Empty user/password
        * SQLite Dialect
        * `ddl-auto=update`
    3. Start with that profile active to use the `ghostnet.db` file in the project root.

---

US2

Let's go with US2! here you have **10 short and actionable steps** to close “**Assign Myself to Recover a Ghost Net**” (reservation) with what you already have in the repo and your stack:

**0.9. Domain Service Preparation (OperationResult Pattern)**

*Before implementing reservation endpoints, adjust the business service to stop using exceptions as control flow and return structured results.*

* Add two new service methods:

  * `assignPerson(id, personName)` → returns `OperationResult<GhostNet>`
  * `markRecovered(id)` → returns `OperationResult<GhostNet>`
* `OperationResult` must support at least: `OK`, `NOT_FOUND`, `CONFLICT` (and optionally `INVALID_ARGUMENT` for empty names).
* Internally, these methods call the repository with `findById`, apply status transition rules, and only persist when valid.
* The controller then maps results to HTTP codes:

  * `OK → 200`, `NOT_FOUND → 404`, `CONFLICT → 409`, `INVALID_ARGUMENT → 400`.
* Keep existing `findByIdOrThrow` only for legacy US1 paths; new reservation/recovery flow uses `Optional` and `OperationResult`.
* This step ensures US2 can be implemented consistently, without exceptions as part of the happy/error path.





1. **Nail Criteria and Transitions**

    * Reservation only from `REPORTED` → moves to `RECOVERY_PENDING` and associates the person.
    * If `status != REPORTED` ⇒ 409 Conflict.
    * `personName` mandatory in the operation.
      (This matches your Gherkin criteria and traceability of endpoints/BL exactly.)

2. **Input DTO (REST)**

    * `ReserveRequest { String personName }` with `@NotBlank`.
    * Validation messages ready for i18n if you already use Bean Validation.
    * Validation error case ⇒ 400 (your `ApiExceptionHandler` already maps it).

3. **REST Endpoint**

    * `PATCH /api/ghostnets/{id}/reserve` in `GhostNetRestController#reserve(…​)`.
    * Read `{id}`, validate `ReserveRequest`, delegate to service, map result to `200/404/409`.
    * Return representation of the updated net (includes `status` and `recoveringPersonName`).

4. **Model/Domain Service (without exceptions as flow)**

    * In `GhostNetBusinessLayerService` add `reserve(long id, Person person)` that returns `OperationResult<GhostNet>` with states `OK | NOT_FOUND | CONFLICT`.
    * Implement in `GhostNetBusinessLayerModel#assignTo(Person)` the rule: if `status==REPORTED` and `person != null` ⇒ set person + `RECOVERY_PENDING`; if `person==null` ⇒ `INVALID_ARGUMENT` (you'll map to 400); in other states ⇒ `CONFLICT`.
    * The controller translates `OK→200`, `NOT_FOUND→404`, `CONFLICT→409`, `INVALID_ARGUMENT→400`. (Fits your rules and error handling.)

5. **Web↔BL Mapper**

    * Reuse `PersonWebToBusinessMapper` to create `Person` from `personName`.
    * Ensure the output mapper populates `recoveringPersonName` when a person is assigned. (Already mentioned in your mappers/traceability table.)

6. **Repos / Data Layer**

    * Use `GhostNetDataLayerModelRepository#findById` and `#save`.
    * (Optional but recommended) add **optimistic locking** with `@Version` in the entity to avoid double simultaneous reservations; an `OptimisticLockException` you translate to 409 too.

7. **UI MVC (Reservation Screen)**

    * `GET /ui/ghostnets/{id}/reserve` shows `form-reserve.html` with required `personName` field and net summary.
    * `POST` of the form: either call the **REST endpoint** (`/api/ghostnets/{id}/reserve`) or directly the **service** (keep consistency with how you did US1).
    * Messages: if 409, show “This net can no longer be reserved (current status: …)”; if 400, paint validation on `personName`. (The view is listed in your traceability; POST is missing.)

8. **Tests (Minimum That Must Pass)**

    * `GhostNetRestControllerTest#reserveGhostNet_Success` (REPORTED→RECOVERY_PENDING, person assigned).
    * `…#reserveGhostNet_ValidationError` (`personName=""` ⇒ 400).
    * `…#reserveGhostNet_Conflict` (initial status `RECOVERY_PENDING`/`RECOVERED` ⇒ 409).
    * `GhostNetIntegrationTest#reserveAndRecoverGhostNet` already referenced: confirms the full “happy path”.

9. **Listings/Detail (Status and Person)**

    * In `/ui/ghostnets` and `GET /api/ghostnets` ensure to show `status` and `person` (or “anonymous” if none).
    * The status filter is already defined for US3; it serves to quickly check the `RECOVERY_PENDING` ones.

10. **Error and Contract Checklist**

* **404** when `{id}` not exists (`ResourceNotFoundException` or `NOT_FOUND` from service).
* **400** for `@NotBlank`/`INVALID_ARGUMENT` in `ReserveRequest`.
* **409** for invalid transition/concurrent lock.
* **Idempotency** (optional): if the same `personName` reserves the SAME net twice and it's already `RECOVERY_PENDING` with that person, you can return `200` with the resource as is; if ANOTHER name ⇒ `409`. (Fits the “reservation” semantics.)


