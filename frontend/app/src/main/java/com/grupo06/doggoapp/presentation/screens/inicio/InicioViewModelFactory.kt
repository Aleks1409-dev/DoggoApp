package com.grupo06.doggoapp.presentation.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grupo06.doggoapp.data.repository.CuidadorRepository

class InicioViewModelFactory(private val repository: CuidadorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InicioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}