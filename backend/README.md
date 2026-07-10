# DoggoApp — Backend (AWS Lambda + DynamoDB)

Backend serverless en Python, desplegado como funciones AWS Lambda detrás de API Gateway (REST API), con DynamoDB como base de datos.

## Funciones

| Función Lambda | Ruta | Método | Descripción |
|---|---|---|---|
| `funLogin` | `/login` | `ANY` | Autentica un usuario y emite un JWT (HS256, expira en 5 minutos). |
| `funRegister` | `/register` | `ANY` | Registra un nuevo usuario (rol `user`). |
| `funSitters` | `/sitters` | `ANY` | Lista los usuarios con rol `sitter` (cuidadores). |
| `funServices` | `/services` | `ANY` | Lista los servicios ofrecidos. |
| `funMessage` | `/messages` | `ANY` | Lista (`GET`) y envía (`POST`) mensajes. |
| `funSchedule` | `/schedule/{sitterId}` | `ANY` | Consulta disponibilidad (`GET`) y reserva una cita (`POST`) para un cuidador. |
| `funTokenAuthorizer` | — (Lambda Authorizer) | — | Valida el header `Authorization: Bearer <token>` en las rutas protegidas. |

Todas las funciones usan el método `ANY` de API Gateway y deciden internamente qué hacer según `event["httpMethod"]` — ver `backend/docs/aws-apigateway-cli.md`.

## Base de datos

4 tablas DynamoDB (`PAY_PER_REQUEST`, sin capacidad provisionada):

| Tabla | Usada por | Partition key | Sort key | GSI |
|---|---|---|---|---|
| `doggo-users` | `funLogin`, `funRegister`, `funSitters` | `email` | — | `role-index` |
| `doggo-services` | `funServices` | `id` | — | — |
| `doggo-schedule` | `funSchedule` | `sitter_id` | `sk` | — |
| `doggo-message` | `funMessage` | `id` | — | — |

Ver `backend/docs/modelo-de-datos.md` para el modelo de datos completo (entidades, atributos, relaciones y diagrama entidad-relación) y `backend/docs/aws-dynamodb-cli.md` para los comandos de creación de cada tabla.

## Documentación de despliegue (AWS CLI)

- **`backend/docs/aws-lambda-cli.md`** — crear las 7 funciones Lambda (runtime `python3.14`, rol `LabRole`, layers `bcrypt`/`PyJWT`).
- **`backend/docs/aws-dynamodb-cli.md`** — crear las 4 tablas DynamoDB con sus claves e índices.
- **`backend/docs/aws-apigateway-cli.md`** — crear el API Gateway REST, conectar cada ruta a su Lambda con método `ANY`, y registrar `funTokenAuthorizer` como Lambda Authorizer.
- **`backend/docs/modelo-de-datos.md`** — entidades, relaciones y diagrama entidad-relación.

Orden recomendado de despliegue: tablas DynamoDB → funciones Lambda → API Gateway (cada documento asume que el paso anterior ya existe).

## Estructura

```
backend/
  function/<nombre>/index.py   # código de cada función (un archivo por función, sin capas internas)
  function_layer/               # layers de dependencias: bcrypt, PyJWT (boto3 ya viene en el runtime)
  docs/                         # esta documentación
  test/                         # colección Postman para probar la API
  database/script.sql           # esquema MySQL original — histórico, ya no vigente (el backend usa DynamoDB)
```

## Requisitos

- Cuenta AWS con acceso a Lambda, API Gateway y DynamoDB (o un entorno de laboratorio con el rol `LabRole` ya provisto).
- AWS CLI configurado (`aws configure` o credenciales de CloudShell).
- Python 3.14 (o el runtime más reciente disponible en la cuenta — el código no usa sintaxis exclusiva de 3.14).

## Notas de seguridad conocidas

- El secreto de firma JWT está hardcodeado como literal en `funLogin`/`funTokenAuthorizer` (no en una variable de entorno ni en Secrets Manager); `funTokenAuthorizer` sí soporta sobrescribirlo vía la variable de entorno `JWT_SECRET_KEY`.
- `funMessage` y `funSchedule` confían en los valores `sitter_id`/`client_id` que llegan en el body de la petición en vez de derivarlos del JWT autenticado (riesgo de suplantación/IDOR). No corregido todavía.
