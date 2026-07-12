package com.grupo06.doggoapp.data.local

import com.grupo06.doggoapp.R

/**
 * Información decorativa (no proveniente del backend) usada para enriquecer la UI
 * de listado y perfil mientras el backend no expone ubicación, especialidades,
 * fotos ni reseñas.
 *
 * TODO: Reemplazar con datos reales del backend cuando estén disponibles.
 */
data class DemoCaregiverInfo(
    val nombre: String,
    val email: String,
    val ubicacion: String,
    val experiencia: String,
    val especialidades: List<String>,
    val fotoResId: Int,
    val rating: Double,
    val tipo: String,
    val disponible: Boolean = true,
    val premium: Boolean = false
)

object DemoCaregiverData {

    private val entries = listOf(
        DemoCaregiverInfo(
            nombre = "Ana Ramirez",
            email = "ana.ramirez@hotmail.com",
            ubicacion = "Miraflores, Lima",
            experiencia = "3 años cuidando perros",
            especialidades = listOf("Paseo", "Cuidado diurno"),
            fotoResId = R.drawable.messi,
            rating = 4.7,
            tipo = "Paseador",
            disponible = true,
            premium = true
        ),
        DemoCaregiverInfo(
            nombre = "Carlos Pérez",
            email = "carlos.perez@gmail.com",
            ubicacion = "San Isidro, Lima",
            experiencia = "5 años de experiencia",
            especialidades = listOf("Hospedaje", "Entrenamiento"),
            fotoResId = R.drawable.cr7,
            rating = 4.9,
            tipo = "Hospedaje",
            disponible = true,
            premium = false
        ),
        DemoCaregiverInfo(
            nombre = "Lucía Torres",
            email = "lucia.torres@gmail.com",
            ubicacion = "Barranco, Lima",
            experiencia = "2 años",
            especialidades = listOf("Paseo", "Cuidado nocturno"),
            fotoResId = R.drawable.haaland,
            rating = 4.5,
            tipo = "Paseos",
            disponible = false,
            premium = false
        ),
        DemoCaregiverInfo(
            nombre = "Diego Vargas",
            email = "diego.vargas@gmail.com",
            ubicacion = "Surco, Lima",
            experiencia = "4 años",
            especialidades = listOf("Cuidado diurno", "Hospedaje"),
            fotoResId = R.drawable.yamal,
            rating = 4.8,
            tipo = "Cuidado diurno",
            disponible = true,
            premium = true
        )
    )

    private val byEmail = entries.associateBy { it.email.lowercase() }
    private val byNombre = entries.associateBy { it.nombre.lowercase() }

    /**
     * Busca la información de demostración asociada a un cuidador.
     * El criterio de coincidencia es el email; si no hay email se usa el nombre.
     * Si no existe coincidencia exacta se devuelve una entrada por defecto.
     */
    fun getFor(email: String?, nombre: String?): DemoCaregiverInfo {
        email?.takeIf { it.isNotBlank() }?.let { byEmail[it.lowercase()] }?.let { return it }
        nombre?.takeIf { it.isNotBlank() }?.let { byNombre[it.lowercase()] }?.let { return it }
        return default(nombre ?: "Cuidador")
    }

    private fun default(nombre: String) = DemoCaregiverInfo(
        nombre = nombre,
        email = "",
        ubicacion = "Ubicación no registrada",
        experiencia = "Experiencia no registrada",
        especialidades = listOf("Servicios generales"),
        fotoResId = R.drawable.messi,
        rating = 4.0,
        tipo = "Servicios generales",
        disponible = true,
        premium = false
    )
}
