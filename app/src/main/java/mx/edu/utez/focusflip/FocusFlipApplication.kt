package mx.edu.utez.focusflip

import android.app.Application

class FocusFlipApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
