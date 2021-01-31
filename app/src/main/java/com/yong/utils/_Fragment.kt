package com.yong.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.yong.YongApplication

fun <T: ViewModel?> Fragment.getViewModel(clazz: Class<T>): T {
    val application = (requireContext().applicationContext as YongApplication)
    return ViewModelFactory(application).create(clazz)
}