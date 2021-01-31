package com.yong.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

fun ImageView.setDefaultRequestOptions(requestBuilder: RequestBuilder<Drawable>) {
    var requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
    when (this.scaleType) {
        ImageView.ScaleType.CENTER_CROP -> requestOptions = requestOptions.centerCrop()
        ImageView.ScaleType.FIT_CENTER -> requestOptions = requestOptions.fitCenter()
        ImageView.ScaleType.CENTER_INSIDE -> requestOptions = requestOptions.centerInside()
        else -> requestOptions.fitCenter()
    }
    requestBuilder
        .apply(requestOptions)
        .into(this)
}