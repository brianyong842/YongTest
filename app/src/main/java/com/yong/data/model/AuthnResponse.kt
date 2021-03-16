package com.yong.data.model

import com.google.gson.annotations.SerializedName

data class AuthnResponse(@SerializedName("token") val token: String,
                         @SerializedName("is_password_secure") val isSecure: Boolean)