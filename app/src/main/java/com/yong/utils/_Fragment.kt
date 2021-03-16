package com.yong.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yong.YongApplication

fun <T: ViewModel?> Fragment.getViewModel(clazz: Class<T>): T {
    val application = (requireContext().applicationContext as YongApplication)
    return ViewModelProvider(this, ViewModelFactory(application)).get(clazz)
}