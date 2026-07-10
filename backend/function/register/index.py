import json
import uuid
import datetime
import boto3
import bcrypt
from botocore.exceptions import ClientError

TABLE_NAME = "doggo-users"
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(TABLE_NAME)

CORS_HEADERS = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "OPTIONS,POST",
    "Access-Control-Allow-Headers": "Content-Type, Authorization",
}


def handler(event, context):
    try:
        data = json.loads(event["body"])

        name = data.get("names") + " " + data.get("surnames")
        email = data.get("email")
        password = data.get("password")
        role = "user"
        created_at = datetime.datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S")

        if not all([name, email, password]):
            return {
                "statusCode": 400,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": "Faltan campos obligatorios"}),
            }

        # encriptar contraseña
        password_bytes = password.encode("utf-8")
        hashed = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
        hashed_password = hashed.decode("utf-8")

        # insertar nuevo usuario; ConditionExpression reemplaza el UNIQUE KEY email de MySQL
        try:
            table.put_item(
                Item={
                    "id": str(uuid.uuid4()),
                    "name": name,
                    "email": email,
                    "encrypted_password": hashed_password,
                    "role": role,
                    "created_at": created_at,
                },
                ConditionExpression="attribute_not_exists(email)",
            )
        except ClientError as ce:
            if ce.response["Error"]["Code"] == "ConditionalCheckFailedException":
                return {
                    "statusCode": 409,
                    "headers": CORS_HEADERS,
                    "body": json.dumps(
                        {"error": "El correo electrónico ya está registrado"}
                    ),
                }
            raise

        return {
            "statusCode": 201,
            "headers": CORS_HEADERS,
            "body": json.dumps({"message": "Registro exitoso"}),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }
