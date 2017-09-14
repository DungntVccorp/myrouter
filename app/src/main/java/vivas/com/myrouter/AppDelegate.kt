package vivas.com.myrouter

import android.app.Application


class AppDelegate : Application() {

    companion object {
        var instance: AppDelegate = AppDelegate()
            private set
    }
    override fun onCreate() {
        instance = this
        Engine.instance.start()
        super.onCreate()
    }
}
