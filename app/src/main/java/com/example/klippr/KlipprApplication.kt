package com.example.klippr

import android.app.Application
import com.example.klippr.shared.core.ServiceLocator

// @author Samuel Bonifacio
/**
 * Punto de entrada del proceso. Posee el [ServiceLocator] que cablea la infraestructura
 * compartida y los Stores por contexto. Los ViewModels lo leen via `application.serviceLocator`.
 */
class KlipprApplication : Application() {

    lateinit var serviceLocator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        serviceLocator = ServiceLocator(this)
    }
}
