package com.yong.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.transition.Fade

abstract class AbsFragment : Fragment() {
    protected val mActivity: AppCompatActivity
        get() = activity as AppCompatActivity

    init {
        val fade = Fade().apply {
            duration = 300
        }
        enterTransition = fade
        exitTransition = fade
    }
}