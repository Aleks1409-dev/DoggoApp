package com.grupo06.doggoapp.presentation.screens.programarCita

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grupo06.doggoapp.data.repository.AgendaRepository
import com.grupo06.doggoapp.data.repository.CuidadorRepository

class ProgramarCitaViewModelFactory(
    private val sitterId: String,
    private val agendaRepository: AgendaRepository,
    private val cuidadorRepository: CuidadorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgramarCitaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgramarCitaViewModel(sitterId, agendaRepository, cuidadorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
