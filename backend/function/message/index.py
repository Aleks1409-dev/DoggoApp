import json
import uuid
import datetime
import boto3

TABLE_NAME = "doggo-message"
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(TABLE_NAME)

CORS_HEADERS = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "OPTIONS,GET,POST",
    "Access-Control-Allow-Headers": "Content-Type, Authorization",
}


def handler(event, context):
    http_method = event.get("httpMethod", "")

    if http_method == "GET":
        return obtener_mensajes()

    if http_method == "POST":
        return enviar_mensaje(event)

    return {
        "statusCode": 405,
        "headers": CORS_HEADERS,
        "body": json.dumps({"message": f"Method {http_method} not allowed"}),
    }


def obtener_mensajes():
    try:
        response = table.scan()
        results = response.get("Items", [])

        return {
            "statusCode": 200,
            "headers": {**{"Content-Type": "application/json"}, **CORS_HEADERS},
            "body": json.dumps(results),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }


def enviar_mensaje(event):
    try:
        data = json.loads(event["body"])

        content = data.get("content")
        sitter_id = data.get("sitter_id")
        client_id = data.get("client_id")

        # validar campos obligatorios
        if not all([content, sitter_id, client_id]):
            return {
                "statusCode": 400,
                "headers": CORS_HEADERS,
                "body": json.dumps(
                    {
                        "error": "Faltan campos obligatorios: content, sitter_id, client_id"
                    }
                ),
            }

        message_id = str(uuid.uuid4())
        created_at = datetime.datetime.utcnow().isoformat()  # UTC timestamp

        table.put_item(
            Item={
                "id": message_id,
                "content": content,
                "sitter_id": sitter_id,
                "client_id": client_id,
                "created_at": created_at,
            }
        )

        return {
            "statusCode": 201,
            "headers": CORS_HEADERS,
            "body": json.dumps(
                {
                    "message": "Mensaje enviado correctamente",
                    "message_id": message_id,
                }
            ),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }
