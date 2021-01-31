package com.yong.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.yong.databinding.DetailFragmentBinding
import com.yong.ui.AbsFragment
import com.yong.utils.getViewModel

open class DetailFragment: AbsFragment() {
    companion object {
        private val TAG = DetailFragment::class.java.simpleName
        private const val ARGS_STORE_ID = "storeId"
        private const val ARGS_STORE_NAME = "storeName"
        fun newInstance(
            fragmentManager: FragmentManager,
            @IdRes id: Int,
            storeId: Long,
            storeName: String
        ) {
            val mFragment = fragmentManager.findFragmentByTag(TAG) as? DetailFragment
            if (mFragment != null) return
            val bundle = Bundle().apply {
                putLong(ARGS_STORE_ID, storeId)
                putString(ARGS_STORE_NAME, storeName)
            }
            val fragment = DetailFragment()

            fragment.arguments = bundle
            fragmentManager.beginTransaction()
                .replace(id, fragment)
                .addToBackStack(TAG)
                .commit()
        }
    }

    private val viewModel by lazy { getViewModel(DetailViewModel::class.java) }

    private lateinit var viewDataBinding: DetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments!!
        viewModel.storeId = bundle.getLong(ARGS_STORE_ID)
        viewDataBinding = DetailFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        mActivity.setSupportActionBar(viewDataBinding.toolbar)
        setActionBar(bundle.getString(ARGS_STORE_NAME, ""))
        viewModel.fetchDetails()

        return viewDataBinding.root
    }

    private fun setActionBar(title: String) {
        val actionBar = mActivity.supportActionBar ?: return
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayShowCustomEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = title
    }
}