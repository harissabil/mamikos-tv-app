package id.harissabil.mamikostvapp

import android.app.Application
import id.harissabil.mamikostvapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MamikosTvApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.ERROR)
            }
            androidContext(this@MamikosTvApp)
            modules(appModules)
        }
    }
}
