package com.yong.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yong.YongApplication
import com.yong.ui.detail.DetailViewModel
import com.yong.ui.main.MainViewModel

class ViewModelFactory(private val application: YongApplication): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application.applicationContext, application.apiHelper)
                isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(application.applicationContext, application.apiHelper)
                else -> super.create(modelClass)
            } as T
        }
    }
}