package com.yong.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.yong.R
import com.yong.data.model.Store
import com.yong.ui.main.adapter.StoreAdapter

@BindingAdapter("app:items")
fun <T> setItems(recyclerView: RecyclerView, items: MutableList<T>) {
    val adapter = recyclerView.adapter
    if (adapter is StoreAdapter) {
        adapter.items = items as MutableList<Store>
    }
}

@BindingAdapter(value = ["app:imageUrl", "app:placeholderImage", "app:errorImage"], requireAll = false )
fun setImage(imageView: ImageView, url: String?, placeholderImage: Drawable?, errorImage: Drawable?) {
    if (url == null) return
    val request = Glide.with(imageView).load(url)
    if (placeholderImage != null) {
        request.placeholder(placeholderImage)
    }
    if (errorImage != null) {
        request.error(errorImage)
    }
    imageView.setDefaultRequestOptions(request)
}