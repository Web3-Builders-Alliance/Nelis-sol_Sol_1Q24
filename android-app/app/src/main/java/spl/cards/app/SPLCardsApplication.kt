package spl.cards.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import spl.cards.app.di.apiModule
import spl.cards.app.di.repositoryModule
import spl.cards.app.di.useCaseModule
import spl.cards.app.di.viewModelModule

class SPLCardsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.ERROR)
            }
            androidContext(androidContext = this@SPLCardsApplication)
            modules(listOf(viewModelModule, useCaseModule, apiModule, repositoryModule))
        }
    }
}
