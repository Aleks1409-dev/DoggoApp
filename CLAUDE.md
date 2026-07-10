# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository structure

This is a monorepo with two independently deployed halves that share no build tooling:

- `frontend/` — native Android app (Kotlin, Jetpack Compose), single Gradle module `app`.
- `backend/` — AWS Lambda functions (Python) behind API Gateway, backed by DynamoDB.

`README.md` at the repo root describes an unrelated Angular project ("Proyecto Doggo Web", `ng serve`) — it does not match this repository's actual contents and should be ignored.

## Frontend (Android)

All commands run from `frontend/`. `gradlew` is not marked executable in this checkout — invoke it as `bash gradlew ...` or `chmod +x gradlew` first.

- Build debug APK: `bash gradlew assembleDebug`
- Unit tests: `bash gradlew test` (single test: `bash gradlew test --tests "com.Piero.doggoapp.ExampleUnitTest"`)
- Instrumented tests (requires a connected device/emulator): `bash gradlew connectedAndroidTest`
- Lint: `bash gradlew lint`

Toolchain: Kotlin 2.2.10, AGP 9.2.1, JDK 21 (`frontend/gradle/gradle-daemon-jvm.properties`), `minSdk` 24 / `targetSdk` 37.

### Architecture

- **No DI framework** (no Hilt/Dagger/Koin). `di/AppContainer.kt` manually builds the single Retrofit instance and repositories. `MainActivity` creates one `AppContainer` and threads it down through `AppNavigation` to each screen, which builds its `ViewModel` via a hand-written `XxxViewModelFactory` (see `presentation/screens/inicio/`).
- **Networking**: Retrofit + Gson converter, one `ApiService` interface in `data/remote/`, base URL hardcoded in `AppContainer.kt`.
- **Screens**: `presentation/screens/<name>/`. Only `inicio` has the full `Screen` + `ViewModel` + `UiState` + `ViewModelFactory` set; `agenda`, `mensajes`, `perfil`, and `bienvenida` are currently static placeholder composables with no view-model wiring.
- **Navigation**: `presentation/navigation/AppNavigation.kt` (Navigation-Compose `NavHost`) plus `NavRutas.kt` for route constants and per-route titles.
- **Cross-cutting UI events** (snackbars, etc.) go through `presentation/events/EventBus.kt`, a `MutableSharedFlow<UiEvent>` singleton — don't thread event callbacks through the view-model layer, emit onto this bus instead.
- Package name is `com.piero.doggoapp` everywhere **except** `DoggoApplication.kt`, which is declared under `com.Piero.doggoapp` (capital P) and is not referenced anywhere (no `android:name` in the manifest) — treat it as dead code, not as the app's actual `Application` class.

**Known gap**: `ApiService.getCuidadores()` calls a `CuidadoresApi` path that the deployed backend does not implement (see backend routes below). `CuidadorRepository.obtenerCuidadores()` is currently a stub — it logs the raw response and returns an empty list rather than parsing it.

## Backend (AWS Lambda, Python + DynamoDB)

`backend/function/` has 7 standalone Lambdas, each a single `index.py` with a `handler(event, context)` entrypoint (REST API v1 event shape — `event.httpMethod`, `event.pathParameters`, `event.body`), deployed individually behind API Gateway: `TokenAuthorizer`, `login`, `register`, `sitters`, `services`, `message` (handles both listing and sending — GET/POST branch on `httpMethod`), `schedule` (handles both availability lookup and booking, same branching). There is no shared framework, no `requirements.txt`/`Pipfile`, and no local runner or test suite.

- **Dependencies**: `boto3`/`botocore` ship preinstalled in the standard Lambda Python runtime — no layer needed for DynamoDB access. `bcrypt` and `PyJWT` are still needed as Lambda layers (prebuilt zips under `backend/function_layer/`). `backend/function_layer/PyMySQL-1.1.1.zip` is now **unused** (left in the repo, not attached to any function going forward) since every function moved off MySQL.
- **Auth**: `login` issues a JWT (HS256, 5-minute expiry) after checking the password with `bcrypt`. `TokenAuthorizer` is the API Gateway TOKEN-type Lambda authorizer that validates the `Authorization: Bearer <token>` header on protected routes.
- **No IaC in this repo** (no SAM/CDK/Terraform/serverless.yml) — DynamoDB tables and their GSIs are provisioned manually (console/CLI) and are not created by anything in this codebase. `backend/database/script.sql` is the old MySQL dump, kept only as historical reference for field names/relationships — it no longer reflects the live data model.
- **DynamoDB tables** (names are hardcoded per-file as `TABLE_NAME`, matching how RDS host/credentials used to be hardcoded — not env vars):
  - `doggo-users` — PK `email` (used directly for `login`'s `get_item` and `register`'s `put_item` with `ConditionExpression: attribute_not_exists(email)`, replacing the MySQL `UNIQUE KEY`). GSI `role-index` (PK `role`) backs `sitters`. A generated `id` (UUID, set in `register`) is what travels everywhere else as `sitter_id`/`client_id`.
  - `doggo-services` — PK `id`; `services` does a full `Scan` (same as the original `SELECT * FROM services`). Note `price` comes back from boto3 as `Decimal` — `services/index.py` has a `_json_default` helper to serialize it, needed anywhere else a numeric DynamoDB attribute is returned directly.
  - `doggo-schedule` — **single table** for both open availability slots and booked appointments, PK `sitter_id` + SK `sk`. Slots: `sk = "SLOT#<date>#<range>"`. Appointments: `sk = "APPOINTMENT#<uuid>"`. `GET /schedule/{sitterId}` queries with `begins_with(sk, "SLOT#")`; `POST /schedule/{sitterId}` (booking) checks availability the same way, then books via `dynamodb_client.transact_write_items` (Delete the slot + Put the appointment) — this is on the **low-level client**, not the `Table` resource, so `schedule/index.py` has a `TypeSerializer`-based `_to_dynamo_item` helper to convert plain dicts to DynamoDB's `{"S": "..."}` wire format for `Key`/`Item`. A `TransactionCanceledException` (race: slot booked between the check and the transaction) maps to the same "no disponible" 400 the original availability check returns. There is no endpoint to *create* slots (there never was — they were seeded straight into MySQL) — `doggo-schedule` starts empty and needs slot items seeded by hand for testing.
  - `doggo-message` — PK `id`; `message`'s GET scans the whole table (matches the original unfiltered `SELECT * FROM messages`), POST puts a new item.
- `backend/test_api/Doggo API.postman_collection.json` documents the previously deployed routes — the route shapes are still accurate, but the collection predates both the DynamoDB migration and the `message`/`schedule` consolidation.

**Security note**: `login`/`TokenAuthorizer` still have the JWT signing secret hardcoded in plaintext (same literal in both files — keep them in sync if it's ever rotated). `message`/`schedule` still trust `sitter_id`/`client_id` values from the request body rather than deriving them from the authenticated caller (IDOR) — this was deliberately left as-is this session to preserve original behavior while migrating the data layer; flagged repeatedly in this conversation, not yet fixed.
