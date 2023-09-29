package com.example.pedemo.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pedemo.domain.SocketRepository
import javax.inject.Inject

class SocketViewModelFactory @Inject constructor(
    private val socketRepository: SocketRepository,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SocketViewModel(
            socketRepository, app
        ) as T
    }
}