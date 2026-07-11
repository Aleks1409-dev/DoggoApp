package com.grupo06.doggoapp.di

import android.content.Context
import com.grupo06.doggoapp.presentation.viewmodel.InicioViewModel
import com.grupo06.doggoapp.presentation.viewmodel.LoginViewModel
import com.grupo06.doggoapp.presentation.viewmodel.PerfilViewModel
import com.grupo06.doggoapp.presentation.viewmodel.RegistroViewModel

class AppContainer(context: Context) {

    private val networkModule: NetworkModule by lazy { NetworkModule(context) }
    private val repositoryModule: RepositoryModule by lazy { RepositoryModule(networkModule) }
    private val useCaseModule: UseCaseModule by lazy { UseCaseModule(repositoryModule) }

    val loginViewModel: LoginViewModel by lazy {
        LoginViewModel(useCaseModule.tokenUseCases)
    }

    val registroViewModel: RegistroViewModel by lazy {
        RegistroViewModel(useCaseModule.tokenUseCases)
    }

    val inicioViewModel: InicioViewModel by lazy {
        InicioViewModel(useCaseModule.cuidadorUseCases)
    }

    val perfilViewModel: PerfilViewModel by lazy {
        PerfilViewModel(useCaseModule.tokenUseCases)
    }
}
