package com.yong.utils

import androidx.lifecycle.ViewModel

interface OnItemClickListener<V: ViewModel> {
    fun onClick(value: V)
}
