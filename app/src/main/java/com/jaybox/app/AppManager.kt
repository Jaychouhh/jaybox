package com.jaybox.app

import android.content.Context

object AppManager {
    @JvmStatic
    val mBlackBoxLoader by lazy {
        JayboxLoader()
    }

    fun doAttachBaseContext(context: Context) {
        try {
            mBlackBoxLoader.attachBaseContext(context)
            mBlackBoxLoader.addLifecycleCallback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doOnCreate(context: Context) {
        mBlackBoxLoader.doOnCreate(context)
        initThirdService(context)
    }

    private fun initThirdService(context: Context) {}
}