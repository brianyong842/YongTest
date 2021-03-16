package com.yong

import android.app.Application
import com.yong.data.api.ApiHelper

class YongApplication: Application() {
    lateinit var apiHelper: ApiHelper
    override fun onCreate() {
        super.onCreate()
        apiHelper = ApiHelper(this)
    }
}