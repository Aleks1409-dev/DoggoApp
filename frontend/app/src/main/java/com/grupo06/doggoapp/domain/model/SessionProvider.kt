package com.grupo06.doggoapp.domain.model

/**
 * Proveedor temporal de identidad de cliente.
 *
 * TODO(HU005): Reemplazar por autenticación real (login / token / perfil de usuario).
 * El valor actual es un UUID de demostración para poder probar el flujo de reservas.
 */
object SessionProvider {
    val clienteId: String = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
