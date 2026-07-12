package com.grupo06.doggoapp.presentation.screens.cuidadorDetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grupo06.doggoapp.data.repository.CuidadorRepository

class CuidadorDetalleViewModelFactory(
    private val sitterId: String,
    private val repository: CuidadorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CuidadorDetalleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CuidadorDetalleViewModel(sitterId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
