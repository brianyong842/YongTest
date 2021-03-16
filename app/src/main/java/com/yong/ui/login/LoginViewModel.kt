package com.yong.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yong.data.api.ApiHelper
import com.yong.data.model.AuthnResponse

class LoginViewModel(private val context: Context,
                     private val apiHelper: ApiHelper) : ViewModel() {
    val errorText = MutableLiveData("")

    fun performAuthn(account: String, password: String, listener: ApiHelper.ResponseListener<AuthnResponse>) {
        apiHelper.performAuthn(account, password, listener)
    }

}