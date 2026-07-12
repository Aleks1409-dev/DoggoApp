package com.grupo06.doggoapp.presentation.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    suspend fun send(event: UiEvent){
        _events.emit(event)
    }
}