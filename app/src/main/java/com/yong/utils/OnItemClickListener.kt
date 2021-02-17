package com.yong.utils

import androidx.lifecycle.ViewModel

interface OnItemClickListener<V: ViewModel> {
    fun onClickAction(value: V, action: Int)
}
