package com.yong.data.model

import com.google.gson.annotations.SerializedName

data class StoreResponse(
    @SerializedName("num_results") val results: Int,
    @SerializedName("next_offset") val nextOffset: Int?,
    @SerializedName("stores") val stores: List<Store>
)