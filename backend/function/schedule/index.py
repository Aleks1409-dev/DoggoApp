import json
import uuid
import datetime
import boto3
from boto3.dynamodb.conditions import Key
from boto3.dynamodb.types import TypeSerializer
from botocore.exceptions import ClientError

TABLE_NAME = "doggo-schedule"
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(TABLE_NAME)
dynamodb_client = boto3.client("dynamodb")

CORS_HEADERS = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "OPTIONS,GET,POST",
    "Access-Control-Allow-Headers": "Content-Type, Authorization",
}

_serializer = TypeSerializer()


def _to_dynamo_item(item):
    # transact_write_items vive en el cliente de bajo nivel: necesita el
    # formato {"S": "valor"} en vez de los tipos nativos de Python.
    return {k: _serializer.serialize(v) for k, v in item.items()}


def handler(event, context):
    http_method = event.get("httpMethod", "")

    # extraer sitterId de pathParameters (compartido por GET y POST)
    sitter_id_raw = event.get("pathParameters", {}).get("sitterId")
    if not sitter_id_raw:
        return {
            "statusCode": 400,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": "Falta parámetro sitterId"}),
        }

    sitter_id = sitter_id_raw

    if http_method == "GET":
        return obtener_agenda(sitter_id)

    if http_method == "POST":
        return agendar_cita(sitter_id, event)

    return {
        "statusCode": 405,
        "headers": CORS_HEADERS,
        "body": json.dumps({"message": f"Method {http_method} not allowed"}),
    }


def _get_slots(sitter_id):
    # doggo-schedule guarda slots abiertos y citas reservadas en la misma
    # tabla; begins_with(sk, "SLOT#") filtra solo los slots sin tocar
    # las citas ya reservadas del mismo sitter.
    response = table.query(
        KeyConditionExpression=Key("sitter_id").eq(sitter_id)
        & Key("sk").begins_with("SLOT#"),
    )
    return response.get("Items", [])


def obtener_agenda(sitter_id):
    try:
        slots = _get_slots(sitter_id)

        if not slots:
            return {
                "statusCode": 404,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": "No se encontró agenda para el cuidador"}),
            }

        # devuelve los días disponibles y los rangos
        days_available = [
            {
                "appointment_date": sched["appointment_date"],
                "appointment_range": sched["appointment_range"],
            }
            for sched in slots
        ]

        return {
            "statusCode": 200,
            "headers": CORS_HEADERS,
            "body": json.dumps({"days_available": days_available}),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }


def agendar_cita(sitter_id, event):
    try:
        data = json.loads(event.get("body", "{}"))

        client_id = data.get("client_id")
        appointment_date_str = data.get("appointment_date")
        appointment_range = data.get("appointment_range")

        if not all([client_id, appointment_date_str, appointment_range]):
            return {
                "statusCode": 400,
                "headers": CORS_HEADERS,
                "body": json.dumps(
                    {
                        "error": "Faltan campos obligatorios: client_id, appointment_date, appointment_range"
                    }
                ),
            }

        try:
            datetime.datetime.strptime(appointment_date_str, "%Y-%m-%d")
        except ValueError:
            return {
                "statusCode": 400,
                "headers": CORS_HEADERS,
                "body": json.dumps(
                    {"error": "Formato inválido para appointment_date. Usa YYYY-MM-DD"}
                ),
            }

        # consultar la disponibilidad del cuidador
        slots = _get_slots(sitter_id)

        if not slots:
            return {
                "statusCode": 404,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": "No se encontró agenda para el cuidador"}),
            }

        # comprobar día y el rango están disponibles
        slot_sk = f"SLOT#{appointment_date_str}#{appointment_range}"
        is_available = any(sched["sk"] == slot_sk for sched in slots)

        if not is_available:
            return {
                "statusCode": 400,
                "headers": CORS_HEADERS,
                "body": json.dumps(
                    {"error": "El día o el rango solicitado no está disponible"}
                ),
            }

        appointment_id = str(uuid.uuid4())
        created_at = datetime.datetime.utcnow().isoformat()

        try:
            # bloquear el slot y crear la cita en una única transacción
            # (equivalente al DELETE + INSERT dentro del commit de MySQL)
            dynamodb_client.transact_write_items(
                TransactItems=[
                    {
                        "Delete": {
                            "TableName": TABLE_NAME,
                            "Key": _to_dynamo_item(
                                {"sitter_id": sitter_id, "sk": slot_sk}
                            ),
                            "ConditionExpression": "attribute_exists(sk)",
                        }
                    },
                    {
                        "Put": {
                            "TableName": TABLE_NAME,
                            "Item": _to_dynamo_item(
                                {
                                    "sitter_id": sitter_id,
                                    "sk": f"APPOINTMENT#{appointment_id}",
                                    "item_type": "APPOINTMENT",
                                    "id": appointment_id,
                                    "client_id": client_id,
                                    "status": "Agendada",
                                    "appointment_date": appointment_date_str,
                                    "appointment_range": appointment_range,
                                    "created_at": created_at,
                                }
                            ),
                        }
                    },
                ]
            )
        except ClientError as ce:
            if ce.response["Error"]["Code"] == "TransactionCanceledException":
                # el slot se reservó entre el chequeo de disponibilidad y la transacción
                return {
                    "statusCode": 400,
                    "headers": CORS_HEADERS,
                    "body": json.dumps(
                        {"error": "El día o el rango solicitado no está disponible"}
                    ),
                }
            raise

        return {
            "statusCode": 201,
            "headers": CORS_HEADERS,
            "body": json.dumps(
                {
                    "message": "Cita agendada correctamente",
                    "appointment_date": appointment_date_str,
                    "appointment_range": appointment_range,
                }
            ),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }
