# DynamoDB — tablas y creación por AWS CLI

Este documento describe las 4 tablas DynamoDB que usa el backend, qué función Lambda usa cada una, su diseño de claves/índices, y los comandos exactos de `aws dynamodb create-table` para crearlas. No hay Infraestructura-como-Código en este repo (sin SAM/CDK/Terraform/serverless.yml) — las tablas se crean a mano con estos comandos.

Todas las tablas usan **billing-mode `PAY_PER_REQUEST`** (on-demand), así que no hace falta planificar capacidad de lectura/escritura.

## Resumen

| Tabla | Función(es) que la usan | Partition key | Sort key | GSI |
|---|---|---|---|---|
| `doggo-users` | `login`, `register`, `sitters` | `email` (S) | — | `role-index` (PK `role`) |
| `doggo-services` | `services` | `id` (S) | — | — |
| `doggo-schedule` | `schedule` | `sitter_id` (S) | `sk` (S) | — |
| `doggo-message` | `message` | `id` (S) | — | — |

`TokenAuthorizer` no usa ninguna tabla (solo valida el JWT).

---

## `doggo-users`

Usada por `login` (busca por email), `register` (crea usuarios) y `sitters` (lista usuarios con `role = "sitter"`). Se usa una sola tabla para las tres porque comparten la misma entidad — si tuvieran tablas separadas, un usuario registrado nunca podría hacer login.

- **Partition key**: `email` (String) — permite `GetItem` directo en `login` y `ConditionExpression: attribute_not_exists(email)` en `register` para evitar emails duplicados (reemplaza el `UNIQUE KEY email` de MySQL).
- **GSI `role-index`**: partition key `role` (String), proyección `ALL` — usada por `sitters` para listar `role = "sitter"` sin escanear toda la tabla.
- **Atributos del item**: `id` (String, UUID generado en `register`, es el valor que viaja como `sitter_id`/`client_id` en el resto de tablas), `name`, `email`, `encrypted_password`, `role` (`"user"` o `"sitter"`), `created_at`.

Ejemplo de item:

```json
{
  "id": "3f9a2c1e-...uuid...",
  "name": "Ana Ramirez",
  "email": "ana.ramirez@hotmail.com",
  "encrypted_password": "$2b$12$....",
  "role": "sitter",
  "created_at": "2026-07-10 02:30:00"
}
```

Comando de creación:

```bash
aws dynamodb create-table \
  --table-name doggo-users \
  --attribute-definitions \
      AttributeName=email,AttributeType=S \
      AttributeName=role,AttributeType=S \
  --key-schema AttributeName=email,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[
    {
      "IndexName": "role-index",
      "KeySchema": [{"AttributeName": "role", "KeyType": "HASH"}],
      "Projection": {"ProjectionType": "ALL"}
    }
  ]'
```

> Nota: solo se declaran en `--attribute-definitions` los atributos que participan en alguna clave (tabla o GSI) — `email` y `role`. El resto de atributos (`id`, `name`, `encrypted_password`, `created_at`) no se declaran porque DynamoDB no tiene esquema fijo fuera de las claves.

Insertar el ejemplo anterior:

```bash
aws dynamodb put-item --table-name doggo-users --item '{
  "id": {"S": "3f9a2c1e-4b8d-4a1f-9c2e-7d6f5a3b1c9e"},
  "name": {"S": "Ana Ramirez"},
  "email": {"S": "ana.ramirez@hotmail.com"},
  "encrypted_password": {"S": "$2b$12$KIXQ4z5v7h9m2wP1sT3fSeJcYQ9pQeR6vD4mB8nL0cW2xH5jK7oGi"},
  "role": {"S": "sitter"},
  "created_at": {"S": "2026-07-10 02:30:00"}
}'
```

> El valor de `encrypted_password` de arriba es solo ilustrativo (no es un hash real). Para probar `login` con un usuario sembrado a mano hace falta un hash bcrypt válido — genera uno con:
> ```bash
> python3 -c "import bcrypt; print(bcrypt.hashpw(b'password123', bcrypt.gensalt()).decode())"
> ```
> y úsalo en `encrypted_password`. Lo más simple, de todos modos, es crear el usuario vía `POST /register` en vez de sembrarlo directo — así el hash siempre queda correcto.

---

## `doggo-services`

Usada por `services` (listado completo vía `Scan`, equivalente al `SELECT * FROM services` original).

- **Partition key**: `id` (String).
- **Sin sort key, sin GSI** — no hay ningún endpoint que filtre por `sitter_id` u otro campo, solo lista todo.
- **Atributos del item**: `id`, `title`, `price` (numérico — boto3 lo devuelve como `Decimal`, ya manejado en `services/index.py`), `sitter_id`, `created_at`.

Ejemplo de item:

```json
{
  "id": "b1e4...uuid...",
  "title": "Paseo",
  "price": 25,
  "sitter_id": "3f9a2c1e-...uuid-de-un-sitter...",
  "created_at": "2026-07-10 02:30:00"
}
```

Comando de creación:

```bash
aws dynamodb create-table \
  --table-name doggo-services \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
```

Insertar el ejemplo anterior:

```bash
aws dynamodb put-item --table-name doggo-services --item '{
  "id": {"S": "b1e4c2d3-6f8a-4e1b-9d2c-5a7f3e9b1c4d"},
  "title": {"S": "Paseo"},
  "price": {"N": "25"},
  "sitter_id": {"S": "3f9a2c1e-4b8d-4a1f-9c2e-7d6f5a3b1c9e"},
  "created_at": {"S": "2026-07-10 02:30:00"}
}'
```

> Nota: en el formato de bajo nivel de `put-item`, los números siempre se envían como `{"N": "<valor-como-string>"}`, aunque representen un `int`/`float`. El `sitter_id` de este ejemplo debe coincidir con el `id` de un item real de `doggo-users`.

---

## `doggo-schedule`

Usada por `schedule`, que maneja dos flujos: consultar disponibilidad (`GET /schedule/{sitterId}`) y reservar una cita (`POST /schedule/{sitterId}`). Ambos conceptos — horarios abiertos ("slots") y citas ya reservadas ("appointments") — viven en la **misma tabla**, diferenciados por el prefijo de la sort key.

- **Partition key**: `sitter_id` (String).
- **Sort key**: `sk` (String), con dos formatos posibles:
  - `SLOT#<appointment_date>#<appointment_range>` — un horario abierto disponible para reservar. Atributo `item_type = "SLOT"`.
  - `APPOINTMENT#<uuid>` — una cita ya confirmada. Atributo `item_type = "APPOINTMENT"`.
- **Sin GSI** — todas las consultas actuales (disponibilidad y reserva) se resuelven con `sitter_id` + `begins_with(sk, "SLOT#")`, sin necesitar un índice adicional.

Esta forma de sort key permite:
- Traer solo los slots abiertos de un sitter con un único `Query` (`sitter_id = X AND begins_with(sk, "SLOT#")`), sin tocar sus citas ya reservadas.
- Reservar de forma atómica con `transact_write_items`: `Delete` del item `SLOT#...` + `Put` del nuevo item `APPOINTMENT#...`, ambos con la misma partition key `sitter_id` (requisito de las transacciones de DynamoDB dentro de la misma partición).

Ejemplo de item **slot** (horario abierto):

```json
{
  "sitter_id": "3f9a2c1e-...uuid-de-un-sitter...",
  "sk": "SLOT#2026-07-20#08:00-12:00",
  "item_type": "SLOT",
  "appointment_date": "2026-07-20",
  "appointment_range": "08:00-12:00"
}
```

Ejemplo de item **appointment** (cita reservada, la crea el propio `schedule` al reservar):

```json
{
  "sitter_id": "3f9a2c1e-...uuid-de-un-sitter...",
  "sk": "APPOINTMENT#8c2b...uuid-de-la-cita...",
  "item_type": "APPOINTMENT",
  "id": "8c2b...uuid-de-la-cita...",
  "client_id": "d4e5...uuid-del-cliente...",
  "status": "Agendada",
  "appointment_date": "2026-07-20",
  "appointment_range": "08:00-12:00",
  "created_at": "2026-07-10T02:30:00.000000"
}
```

Comando de creación:

```bash
aws dynamodb create-table \
  --table-name doggo-schedule \
  --attribute-definitions \
      AttributeName=sitter_id,AttributeType=S \
      AttributeName=sk,AttributeType=S \
  --key-schema \
      AttributeName=sitter_id,KeyType=HASH \
      AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST
```

### Sembrar datos de prueba

No existe (ni existía en la versión original con MySQL) un endpoint para *crear* slots — se insertaban directo en la base de datos. En DynamoDB hay que sembrar el slot a mano para poder probar `GET`/`POST /schedule/{sitterId}`:

Insertar el ejemplo de **slot**:

```bash
aws dynamodb put-item --table-name doggo-schedule --item '{
  "sitter_id": {"S": "3f9a2c1e-4b8d-4a1f-9c2e-7d6f5a3b1c9e"},
  "sk": {"S": "SLOT#2026-07-20#08:00-12:00"},
  "item_type": {"S": "SLOT"},
  "appointment_date": {"S": "2026-07-20"},
  "appointment_range": {"S": "08:00-12:00"}
}'
```

Insertar el ejemplo de **appointment** (normalmente lo crea el propio `POST /schedule/{sitterId}` vía `transact_write_items`; este comando es solo para pruebas manuales o para inspeccionar el formato del item):

```bash
aws dynamodb put-item --table-name doggo-schedule --item '{
  "sitter_id": {"S": "3f9a2c1e-4b8d-4a1f-9c2e-7d6f5a3b1c9e"},
  "sk": {"S": "APPOINTMENT#8c2b1a4d-3e5f-4b7c-9d1a-6f8e2c4b7a3d"},
  "item_type": {"S": "APPOINTMENT"},
  "id": {"S": "8c2b1a4d-3e5f-4b7c-9d1a-6f8e2c4b7a3d"},
  "client_id": {"S": "d4e5f6a7-1b2c-4d3e-8f9a-0b1c2d3e4f5a"},
  "status": {"S": "Agendada"},
  "appointment_date": {"S": "2026-07-20"},
  "appointment_range": {"S": "08:00-12:00"},
  "created_at": {"S": "2026-07-10T02:30:00.000000"}
}'
```

---

## `doggo-message`

Usada por `message` (`GET` lista todos los mensajes sin filtrar vía `Scan`, equivalente al `SELECT * FROM messages` original; `POST` inserta uno nuevo).

- **Partition key**: `id` (String).
- **Sin sort key, sin GSI**.
- **Atributos del item**: `id`, `content`, `sitter_id`, `client_id`, `created_at`.

Ejemplo de item:

```json
{
  "id": "5a7c...uuid...",
  "content": "hola, quiero agendar para el día jueves?",
  "sitter_id": "3f9a2c1e-...uuid-del-sitter...",
  "client_id": "d4e5...uuid-del-cliente...",
  "created_at": "2026-07-10T02:30:00.000000"
}
```

Comando de creación:

```bash
aws dynamodb create-table \
  --table-name doggo-message \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
```

Insertar el ejemplo anterior:

```bash
aws dynamodb put-item --table-name doggo-message --item '{
  "id": {"S": "5a7c8b9d-2e4f-4a6b-8c1d-3e5f7a9b1c2d"},
  "content": {"S": "hola, quiero agendar para el día jueves?"},
  "sitter_id": {"S": "3f9a2c1e-4b8d-4a1f-9c2e-7d6f5a3b1c9e"},
  "client_id": {"S": "d4e5f6a7-1b2c-4d3e-8f9a-0b1c2d3e4f5a"},
  "created_at": {"S": "2026-07-10T02:30:00.000000"}
}'
```

---
Las tablas tardan unos segundos en pasar a estado `ACTIVE`. Para verificar:

```bash
aws dynamodb list-tables

aws dynamodb describe-table --table-name doggo-users --query "Table.TableStatus"
aws dynamodb describe-table --table-name doggo-services --query "Table.TableStatus"
aws dynamodb describe-table --table-name doggo-schedule --query "Table.TableStatus"
aws dynamodb describe-table --table-name doggo-message --query "Table.TableStatus"
```

---