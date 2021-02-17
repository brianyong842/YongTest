package com.yong.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yong.data.model.Store
import com.yong.utils.OnItemClickListener

const val CLICK_ACTION_OPEN = 1
const val CLICK_ACTION_FAVORITE = 2
class StoreViewModel(
    val item: Store,
    private val onClickListener: OnItemClickListener<StoreViewModel>,
    favorite: Boolean,

    ) : ViewModel() {
    val name: String = item.name
    val imageUrl: String = item.image
    val description: String = item.description
    val isFavorite = MutableLiveData(favorite)

    fun openDetail() {
        onClickListener.onClickAction(this, CLICK_ACTION_OPEN)
    }

    fun favoriteAction() {
        onClickListener.onClickAction(this, CLICK_ACTION_FAVORITE)
    }

}