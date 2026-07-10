# Creación de las funciones Lambda por AWS CLI

Este documento describe cómo crear en AWS las 7 funciones Lambda de `backend/function/` desde la línea de comandos (pensado para ejecutarse en **AWS CloudShell**), con:

- **Runtime**: `python3.14`
- **Rol de ejecución**: el rol personalizado `LabRole` (ya existente en el entorno), no uno nuevo creado por estos comandos.
- **Nombre de cada función Lambda**: `fun[Nombre]` (`funTokenAuthorizer`, `funLogin`, `funRegister`, `funSitters`, `funServices`, `funMessage`, `funSchedule`), siguiendo la misma convención que `funLibros` en `guia/`.
- **Código**: el `.zip` de cada función, ya subido en `/home/cloudshell-user/`.

> El nombre de la Lambda (`--function-name`) es `fun[Nombre]`, pero se asume que el **zip** conserva el nombre de la carpeta original en el repo (`login.zip`, `register.zip`, `sitters.zip`, `services.zip`, `message.zip`, `schedule.zip`, `TokenAuthorizer.zip`), ya que es lo que normalmente ya está subido a CloudShell. Si además renombraste los zips a `fun*.zip`, ajusta la ruta en `--zip-file` de cada comando. En ambos casos el zip debe contener `index.py` en su raíz (no dentro de una subcarpeta).

---

## Paso 0 — Obtener el ARN de `LabRole`

```bash
ROLE_ARN=$(aws iam get-role --role-name LabRole --query 'Role.Arn' --output text)
echo "$ROLE_ARN"
```

Todas las funciones usan este mismo `$ROLE_ARN` en `--role`.

---

## Paso 1 — Publicar las layers que faltan (`bcrypt`, `PyJWT`)

`boto3`/`botocore` ya vienen incluidos en el runtime `python3.14`, así que **no necesitan layer**. Solo `bcrypt` y `PyJWT` (usadas por `funLogin`, `funRegister` y `funTokenAuthorizer`) hace falta publicarlas como Lambda Layer. Los zips ya existen en el repo (`backend/function_layer/`); súbelos también a `/home/cloudshell-user/` antes de este paso.

```bash
BCRYPT_LAYER_ARN=$(aws lambda publish-layer-version \
  --layer-name bcrypt \
  --description "bcrypt 4.3.0 para python3.14" \
  --zip-file fileb:///home/cloudshell-user/bcrypt-4.3.0.zip \
  --compatible-runtimes python3.14 \
  --query 'LayerVersionArn' --output text)

PYJWT_LAYER_ARN=$(aws lambda publish-layer-version \
  --layer-name PyJWT \
  --description "PyJWT 2.10.1 para python3.14" \
  --zip-file fileb:///home/cloudshell-user/PyJWT-2.10.1.zip \
  --compatible-runtimes python3.14 \
  --query 'LayerVersionArn' --output text)

echo "$BCRYPT_LAYER_ARN"
echo "$PYJWT_LAYER_ARN"
```

> El layer `PyMySQL-1.1.1.zip` que también está en `backend/function_layer/` **ya no se usa** — todas las funciones migraron de MySQL a DynamoDB (`boto3`). No hace falta publicarlo ni adjuntarlo a ninguna función nueva.

---

## Resumen: función Lambda → zip → handler → layers → variables de entorno

| Nombre de la Lambda | Zip esperado | Handler | Layers | Variables de entorno |
|---|---|---|---|---|
| `funTokenAuthorizer` | `TokenAuthorizer.zip` | `index.handler` | `PyJWT` | `JWT_SECRET_KEY` (opcional, ver nota abajo) |
| `funLogin` | `login.zip` | `index.handler` | `bcrypt`, `PyJWT` | — |
| `funRegister` | `register.zip` | `index.handler` | `bcrypt` | — |
| `funSitters` | `sitters.zip` | `index.handler` | — | — |
| `funServices` | `services.zip` | `index.handler` | — | — |
| `funMessage` | `message.zip` | `index.handler` | — | — |
| `funSchedule` | `schedule.zip` | `index.handler` | — | — |

> `JWT_SECRET_KEY` en `funTokenAuthorizer`: el código hace `os.getenv("JWT_SECRET_KEY", "<secreto-por-defecto>")`, es decir, si no se define la variable de entorno usa automáticamente el mismo literal que ya está hardcodeado en `login/index.py` (el código de `funLogin`) — funciona igual sin configurar nada. Si más adelante decides rotar el secreto, tendrás que definir `JWT_SECRET_KEY` aquí **y** actualizar el literal en `login/index.py` para que ambos sigan coincidiendo (`funLogin` firma el JWT, `funTokenAuthorizer` lo valida).

---

## Paso 2 — Crear cada función

### `funTokenAuthorizer`

```bash
aws lambda create-function \
  --function-name funTokenAuthorizer \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/TokenAuthorizer.zip \
  --layers "$PYJWT_LAYER_ARN" \
  --timeout 10 \
  --memory-size 128
```

### `funLogin`

```bash
aws lambda create-function \
  --function-name funLogin \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/login.zip \
  --layers "$BCRYPT_LAYER_ARN" "$PYJWT_LAYER_ARN" \
  --timeout 10 \
  --memory-size 128
```

### `funRegister`

```bash
aws lambda create-function \
  --function-name funRegister \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/register.zip \
  --layers "$BCRYPT_LAYER_ARN" \
  --timeout 10 \
  --memory-size 128
```

### `funSitters`

```bash
aws lambda create-function \
  --function-name funSitters \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/sitters.zip \
  --timeout 10 \
  --memory-size 128
```

### `funServices`

```bash
aws lambda create-function \
  --function-name funServices \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/services.zip \
  --timeout 10 \
  --memory-size 128
```

### `funMessage`

```bash
aws lambda create-function \
  --function-name funMessage \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/message.zip \
  --timeout 10 \
  --memory-size 128
```

### `funSchedule`

```bash
aws lambda create-function \
  --function-name funSchedule \
  --runtime python3.14 \
  --role "$ROLE_ARN" \
  --handler index.handler \
  --zip-file fileb:///home/cloudshell-user/schedule.zip \
  --timeout 10 \
  --memory-size 128
```

---

## Verificar que las 7 funciones se crearon

```bash
aws lambda list-functions --query "Functions[].FunctionName" --output table
```

Para inspeccionar una función en particular (runtime, handler, layers, rol):

```bash
aws lambda get-function-configuration --function-name funLogin
```

---

## Volver a desplegar después de un cambio

Si editas el código y vuelves a generar el zip, **no** se usa `create-function` de nuevo (fallaría porque la función ya existe) — se actualiza el código con:

```bash
aws lambda update-function-code \
  --function-name funLogin \
  --zip-file fileb:///home/cloudshell-user/login.zip
```

Y si lo que cambia es configuración (runtime, layers, timeout, variables de entorno), con:

```bash
aws lambda update-function-configuration \
  --function-name funTokenAuthorizer \
  --environment "Variables={JWT_SECRET_KEY=tu-nuevo-secreto}"
```

---