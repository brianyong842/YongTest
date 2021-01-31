package com.yong.ui

import androidx.lifecycle.MutableLiveData

interface LoadingViewModel {
    val isLoading: MutableLiveData<Boolean>
}