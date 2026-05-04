package com.example.inentory

import android.app.Application
import com.example.inentory.data.AppContainer
import com.example.inentory.data.AppDataContainer

class InventoryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
