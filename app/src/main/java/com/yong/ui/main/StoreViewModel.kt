package com.yong.ui.main

import androidx.lifecycle.ViewModel
import com.yong.data.model.Store
import com.yong.utils.OnItemClickListener

class StoreViewModel(val item: Store, private val onClickListener: OnItemClickListener<StoreViewModel>) : ViewModel() {
    val name: String = item.name
    val imageUrl: String = item.image
    val description: String = item.description

    fun openDetail() {
        onClickListener.onClick(this)
    }
}