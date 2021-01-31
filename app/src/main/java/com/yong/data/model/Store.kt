package com.yong.data.model

import com.google.gson.annotations.SerializedName

data class Store(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("cover_img_url") val image: String
)