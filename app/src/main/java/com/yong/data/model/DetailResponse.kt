package com.yong.data.model

import com.google.gson.annotations.SerializedName

data class DetailResponse(
    @SerializedName("business") val business: Business,
    @SerializedName("phone_number") val phone: String,
    @SerializedName("cover_img_url") val image: String,
    @SerializedName("status_type") val status: String,
    @SerializedName("description") val description: String,
    @SerializedName("tags") val tags: ArrayList<String>
) {
    val name: String
        get() = business.name
}