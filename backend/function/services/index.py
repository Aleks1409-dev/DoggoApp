import json
import boto3
from decimal import Decimal

TABLE_NAME = "doggo-services"
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(TABLE_NAME)

CORS_HEADERS = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "OPTIONS,GET",
    "Access-Control-Allow-Headers": "Content-Type, Authorization",
}


def _json_default(value):
    # boto3 devuelve los números de DynamoDB (ej. price) como Decimal
    if isinstance(value, Decimal):
        return int(value) if value % 1 == 0 else float(value)
    raise TypeError(f"Object of type {type(value)} is not JSON serializable")


def handler(event, context):
    http_method = event.get("httpMethod", "")

    if http_method == "GET":
        try:
            response = table.scan()
            result = response.get("Items", [])

            return {
                "statusCode": 200,
                "headers": {"Content-Type": "application/json", **CORS_HEADERS},
                "body": json.dumps(result, default=_json_default),
            }

        except Exception as e:
            return {
                "statusCode": 500,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": str(e)}),
            }

    else:
        return {
            "statusCode": 405,
            "headers": CORS_HEADERS,
            "body": json.dumps({"message": f"Method {http_method} not allowed"}),
        }
