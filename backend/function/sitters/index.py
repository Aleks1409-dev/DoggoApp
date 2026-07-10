import json
import boto3
from boto3.dynamodb.conditions import Key

TABLE_NAME = "doggo-users"
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(TABLE_NAME)

CORS_HEADERS = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "OPTIONS,GET",
    "Access-Control-Allow-Headers": "Content-Type, Authorization",
}

def handler(event, context):
    try:
        # doggo-users tiene una GSI role-index para listar por rol sin Scan
        response = table.query(
            IndexName="role-index",
            KeyConditionExpression=Key("role").eq("sitter"),
        )
        sitters = response.get("Items", [])

        # replica el SELECT id, name, email, role, created_at (sin encrypted_password)
        for sitter in sitters:
            sitter.pop("encrypted_password", None)

        return {
            "statusCode": 200,
            "headers": CORS_HEADERS,
            "body": json.dumps({"sitters": sitters}),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }
