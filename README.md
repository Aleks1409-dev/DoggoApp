# DoggoApp

Aplicación para conectar clientes con cuidadores de mascotas ("sitters"): permite buscar cuidadores, ver servicios ofrecidos, agendar citas y enviar mensajes entre cliente y cuidador. El proyecto tiene dos partes independientes:

- **App Android nativa** (`frontend/`) — Kotlin + Jetpack Compose.
- **Backend serverless en AWS** (`backend/`) — funciones Lambda en Python + DynamoDB, detrás de API Gateway.

## Estructura del repositorio

```
DoggoApp/
  frontend/        # app Android (Kotlin, Jetpack Compose) — ver frontend/README.md
  backend/         # funciones Lambda, DynamoDB, docs de despliegue — ver backend/README.md
    function/      # código de las 7 funciones Lambda
    function_layer/# layers de dependencias (bcrypt, PyJWT)
    docs/          # documentación de infraestructura (Lambda/DynamoDB/API Gateway/modelo de datos)
    test/          # colección Postman
    database/      # esquema MySQL original — histórico, el backend actual usa DynamoDB
  guia/            # ejemplo de referencia (funLibros) usado como guía de arquitectura para el backend
```

## Arquitectura general

```
App Android (Retrofit) → API Gateway (REST, método ANY) → Lambdas Python → DynamoDB
                                        ↑
                          funTokenAuthorizer (Lambda Authorizer, valida JWT)
```

- La autenticación se resuelve con JWT: `funLogin` lo emite, `funTokenAuthorizer` lo valida como Lambda Authorizer en las rutas protegidas.
- Cada Lambda usa el método `ANY` de API Gateway y decide internamente qué hacer según el verbo HTTP recibido.
- La base de datos es DynamoDB (4 tablas), no relacional — el proyecto migró desde un esquema MySQL inicial (`backend/database/script.sql`, ya histórico).

## Documentación

| Documento | Contenido |
|---|---|
| [`frontend/README.md`](frontend/README.md) | Cómo compilar y ejecutar la app Android, arquitectura del cliente. |
| [`backend/README.md`](backend/README.md) | Funciones Lambda, tablas DynamoDB, y punto de entrada a la documentación de despliegue. |
| [`backend/docs/aws-lambda-cli.md`](backend/docs/aws-lambda-cli.md) | Crear las funciones Lambda por AWS CLI. |
| [`backend/docs/aws-dynamodb-cli.md`](backend/docs/aws-dynamodb-cli.md) | Crear las tablas DynamoDB por AWS CLI. |
| [`backend/docs/aws-apigateway-cli.md`](backend/docs/aws-apigateway-cli.md) | Crear el API Gateway y conectarlo a cada función por AWS CLI. |
| [`backend/docs/modelo-de-datos.md`](backend/docs/modelo-de-datos.md) | Entidades, atributos, relaciones y diagrama entidad-relación. |

## Estado del proyecto

- El backend (Lambdas + DynamoDB) es funcional de forma independiente y probable vía Postman/consola de API Gateway.
- La app Android todavía no está apuntando a las rutas reales del backend desplegado — ver la sección "Estado conocido" en `frontend/README.md`.
- Hay notas de seguridad pendientes de resolver en el backend (secreto JWT hardcodeado, validación de identidad en `messages`/`schedule`) — ver `backend/README.md`.

## Requisitos generales

- **Frontend**: Android Studio o JDK 21 + Gradle.
- **Backend**: cuenta AWS (Lambda, API Gateway, DynamoDB) y AWS CLI configurado.
