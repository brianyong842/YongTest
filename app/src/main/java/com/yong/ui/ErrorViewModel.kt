package com.yong.ui

import androidx.lifecycle.MutableLiveData

interface ErrorViewModel {
    val showError: MutableLiveData<Boolean>
    val errorMessage: MutableLiveData<String>
    val errorCTA: MutableLiveData<String>
    fun errorCTAClick()
}