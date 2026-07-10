import json
import boto3
import bcrypt
import jwt
import datetime

TABLE_NAME = "doggo-users"
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(TABLE_NAME)

SECRET_KEY = "3jI+eJg94dHhiD6skc7ZACFqXr7G/G/q/OVi7z9U9cNKeXrdFAV2m6vkr3msio5k"

CORS_HEADERS = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "OPTIONS,POST",
    "Access-Control-Allow-Headers": "Content-Type, Authorization",
}

def handler(event, context):
    try:
        data = json.loads(event["body"])

        email = data.get("email")
        password = data.get("password")

        if not all([email, password]):
            return {
                "statusCode": 400,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": "Faltan campos obligatorios"}),
            }

        # doggo-users tiene email como partition key: GetItem directo
        response = table.get_item(Key={"email": email})
        user = response.get("Item")

        if not user:
            return {
                "statusCode": 404,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": "No existe el Usuario"}),
            }

        hashed_password = user["encrypted_password"].encode("utf-8")
        password_bytes = password.encode("utf-8")

        # verificar contraseña con bcrypt
        if not bcrypt.checkpw(password_bytes, hashed_password):
            return {
                "statusCode": 401,
                "headers": CORS_HEADERS,
                "body": json.dumps({"error": "Contraseña incorrecta"}),
            }

        payload = {
            "user_id": user["id"],
            "email": email,
            "role": user["role"],
            "exp": datetime.datetime.utcnow()
            + datetime.timedelta(minutes=5),  # Expira en 5 minutos
        }
        token = jwt.encode(payload, SECRET_KEY, algorithm="HS256")

        return {
            "statusCode": 200,
            "headers": CORS_HEADERS,
            "body": json.dumps({"success": "True", "token": token, "email": email}),
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": CORS_HEADERS,
            "body": json.dumps({"error": str(e)}),
        }
