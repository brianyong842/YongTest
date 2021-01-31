package com.yong

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yong.data.api.ApiHelper

class YongApplication: Application() {
    lateinit var apiHelper: ApiHelper
    override fun onCreate() {
        super.onCreate()
        apiHelper = ApiHelper(this)
    }
}