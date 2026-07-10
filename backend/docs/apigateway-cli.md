# Creación del API Gateway por AWS CLI

Este documento describe cómo crear el **API Gateway REST API** (no HTTP API — se necesita REST API porque `funTokenAuthorizer` es un Lambda Authorizer de tipo `TOKEN`, exclusivo de REST API) y conectar cada ruta a su Lambda correspondiente (`backend/docs/lambda-cli.md`), usando el método **`ANY`** en todas las rutas (así cada Lambda recibe cualquier verbo HTTP y decide internamente qué hacer según `event["httpMethod"]`, tal como ya hacen `funMessage`, `funSchedule` y `funServices`).

Rutas → función Lambda (mismos paths que `backend/test_api/Doggo API.postman_collection.json`):

| Ruta | Método | Lambda |
|---|---|---|
| `/login` | `ANY` | `funLogin` |
| `/register` | `ANY` | `funRegister` |
| `/sitters` | `ANY` | `funSitters` |
| `/services` | `ANY` | `funServices` |
| `/messages` | `ANY` | `funMessage` |
| `/schedule/{sitterId}` | `ANY` | `funSchedule` |

`funTokenAuthorizer` no tiene una ruta propia — se registra como **Lambda Authorizer** y se asocia opcionalmente a los métodos que quieras proteger (ver el último paso).

---

## Paso 0 — Variables base

```bash
REGION=us-east-1
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "$REGION / $ACCOUNT_ID"
```

Todas las funciones deben existir ya (ver `backend/docs/lambda-cli.md`: `funLogin`, `funRegister`, `funSitters`, `funServices`, `funMessage`, `funSchedule`, `funTokenAuthorizer`).

---

## Paso 1 — Crear el REST API y obtener el recurso raíz

```bash
API_ID=$(aws apigateway create-rest-api \
  --name "DoggoApi" \
  --description "API REST del backend de DoggoApp" \
  --endpoint-configuration types=REGIONAL \
  --query 'id' --output text)

ROOT_ID=$(aws apigateway get-resources \
  --rest-api-id "$API_ID" \
  --query 'items[?path==`/`].id' --output text)

echo "API_ID=$API_ID"
echo "ROOT_ID=$ROOT_ID"
```

---

## Paso 2 — Crear cada ruta (recurso + método `ANY` + integración Lambda + permiso)

Cada ruta necesita 4 comandos: crear el recurso, crear el método `ANY`, apuntar la integración a la Lambda (proxy), y darle permiso a API Gateway para invocarla.

### `/login` → `funLogin`

```bash
LOGIN_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$ROOT_ID" \
  --path-part "login" --query 'id' --output text)

aws apigateway put-method \
  --rest-api-id "$API_ID" --resource-id "$LOGIN_ID" \
  --http-method ANY --authorization-type NONE

aws apigateway put-integration \
  --rest-api-id "$API_ID" --resource-id "$LOGIN_ID" \
  --http-method ANY --type AWS_PROXY --integration-http-method POST \
  --uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funLogin/invocations"

aws lambda add-permission \
  --function-name funLogin --statement-id apigateway-login-any \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/*/*/login"
```

### `/register` → `funRegister`

```bash
REGISTER_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$ROOT_ID" \
  --path-part "register" --query 'id' --output text)

aws apigateway put-method \
  --rest-api-id "$API_ID" --resource-id "$REGISTER_ID" \
  --http-method ANY --authorization-type NONE

aws apigateway put-integration \
  --rest-api-id "$API_ID" --resource-id "$REGISTER_ID" \
  --http-method ANY --type AWS_PROXY --integration-http-method POST \
  --uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funRegister/invocations"

aws lambda add-permission \
  --function-name funRegister --statement-id apigateway-register-any \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/*/*/register"
```

### `/sitters` → `funSitters`

```bash
SITTERS_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$ROOT_ID" \
  --path-part "sitters" --query 'id' --output text)

aws apigateway put-method \
  --rest-api-id "$API_ID" --resource-id "$SITTERS_ID" \
  --http-method ANY --authorization-type NONE

aws apigateway put-integration \
  --rest-api-id "$API_ID" --resource-id "$SITTERS_ID" \
  --http-method ANY --type AWS_PROXY --integration-http-method POST \
  --uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funSitters/invocations"

aws lambda add-permission \
  --function-name funSitters --statement-id apigateway-sitters-any \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/*/*/sitters"
```

### `/services` → `funServices`

```bash
SERVICES_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$ROOT_ID" \
  --path-part "services" --query 'id' --output text)

aws apigateway put-method \
  --rest-api-id "$API_ID" --resource-id "$SERVICES_ID" \
  --http-method ANY --authorization-type NONE

aws apigateway put-integration \
  --rest-api-id "$API_ID" --resource-id "$SERVICES_ID" \
  --http-method ANY --type AWS_PROXY --integration-http-method POST \
  --uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funServices/invocations"

aws lambda add-permission \
  --function-name funServices --statement-id apigateway-services-any \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/*/*/services"
```

### `/messages` → `funMessage`

```bash
MESSAGES_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$ROOT_ID" \
  --path-part "messages" --query 'id' --output text)

aws apigateway put-method \
  --rest-api-id "$API_ID" --resource-id "$MESSAGES_ID" \
  --http-method ANY --authorization-type NONE

aws apigateway put-integration \
  --rest-api-id "$API_ID" --resource-id "$MESSAGES_ID" \
  --http-method ANY --type AWS_PROXY --integration-http-method POST \
  --uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funMessage/invocations"

aws lambda add-permission \
  --function-name funMessage --statement-id apigateway-messages-any \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/*/*/messages"
```

### `/schedule/{sitterId}` → `funSchedule`

Esta ruta tiene un parámetro de path, así que primero se crea el recurso intermedio `/schedule` (sin método propio, solo como contenedor) y luego `/schedule/{sitterId}` con el método `ANY`.

```bash
SCHEDULE_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$ROOT_ID" \
  --path-part "schedule" --query 'id' --output text)

SCHEDULE_SITTER_ID=$(aws apigateway create-resource \
  --rest-api-id "$API_ID" --parent-id "$SCHEDULE_ID" \
  --path-part "{sitterId}" --query 'id' --output text)

aws apigateway put-method \
  --rest-api-id "$API_ID" --resource-id "$SCHEDULE_SITTER_ID" \
  --http-method ANY --authorization-type NONE

aws apigateway put-integration \
  --rest-api-id "$API_ID" --resource-id "$SCHEDULE_SITTER_ID" \
  --http-method ANY --type AWS_PROXY --integration-http-method POST \
  --uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funSchedule/invocations"

aws lambda add-permission \
  --function-name funSchedule --statement-id apigateway-schedule-any \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/*/*/schedule/*"
```

> `{sitterId}` llega automáticamente en `event["pathParameters"]["sitterId"]` porque la integración es `AWS_PROXY` — no hace falta declarar `--request-parameters` ni mapeos adicionales, a diferencia de una integración no-proxy.

---

## Paso 3 — Desplegar el API a un stage

Ningún cambio de recursos/métodos/integraciones tiene efecto en la URL pública hasta que se hace un deployment. Se usa el stage `api` para que la URL final quede igual que en `Doggo API.postman_collection.json` (`.../api/login`, `.../api/messages`, etc.):

```bash
aws apigateway create-deployment \
  --rest-api-id "$API_ID" \
  --stage-name api

echo "Invoke URL base: https://${API_ID}.execute-api.${REGION}.amazonaws.com/api"
```

Cada vez que agregues o modifiques una ruta, hay que repetir `create-deployment` (al mismo stage `api`) para que el cambio se publique.

---

## Verificar

```bash
aws apigateway get-resources --rest-api-id "$API_ID" --output table

curl -i -X POST "https://${API_ID}.execute-api.${REGION}.amazonaws.com/api/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"ana.ramirez@hotmail.com","password":"password123"}'

curl -i "https://${API_ID}.execute-api.${REGION}.amazonaws.com/api/sitters"
```

---

## Opcional — Registrar `funTokenAuthorizer` y proteger rutas con él

`funTokenAuthorizer` no es una ruta del API, es un **Lambda Authorizer** que se registra una vez en el API y luego se asocia (opcionalmente) al método `ANY` de las rutas que quieras proteger con JWT.

Registrar el authorizer:

```bash
AUTHORIZER_ID=$(aws apigateway create-authorizer \
  --rest-api-id "$API_ID" \
  --name funTokenAuthorizer \
  --type TOKEN \
  --authorizer-uri "arn:aws:apigateway:${REGION}:lambda:path/2015-03-31/functions/arn:aws:lambda:${REGION}:${ACCOUNT_ID}:function:funTokenAuthorizer/invocations" \
  --identity-source "method.request.header.Authorization" \
  --authorizer-result-ttl-in-seconds 0 \
  --query 'id' --output text)

aws lambda add-permission \
  --function-name funTokenAuthorizer --statement-id apigateway-authorizer \
  --action lambda:InvokeFunction --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:${REGION}:${ACCOUNT_ID}:${API_ID}/authorizers/${AUTHORIZER_ID}"
```

Proteger una ruta ya creada (ejemplo con `/sitters`; repetir por cada `RESOURCE_ID` que quieras exigir token — `$SITTERS_ID`, `$SERVICES_ID`, `$MESSAGES_ID`, `$SCHEDULE_SITTER_ID`, etc.; **no** se le pone a `/login` ni `/register`, porque ahí es donde se obtiene el token):

```bash
aws apigateway update-method \
  --rest-api-id "$API_ID" --resource-id "$SITTERS_ID" --http-method ANY \
  --patch-operations \
      op=replace,path=/authorizationType,value=CUSTOM \
      op=replace,path=/authorizerId,value="$AUTHORIZER_ID"
```

Después de proteger rutas hay que volver a desplegar (`aws apigateway create-deployment --rest-api-id "$API_ID" --stage-name api`) para que el cambio se publique.

---