package com.yong.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.yong.R
import com.yong.REQUEST_LOCATION
import com.yong.databinding.MainFragmentBinding
import com.yong.ui.AbsFragment
import com.yong.ui.detail.DetailFragment
import com.yong.ui.main.adapter.StoreAdapter
import com.yong.utils.getViewModel

const val PERMISSION_GRANTED = 0
const val PERMISSION_DENIED = 1
const val PERMISSION_BLOCKED = 2
class MainFragment : AbsFragment() {
    private val viewModel by lazy { getViewModel(MainViewModel::class.java) }

    private lateinit var viewDataBinding: MainFragmentBinding

    private val locationPermissionStatus: Int
        get() {
            return if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                PERMISSION_GRANTED
            }  else if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PERMISSION_BLOCKED
            } else {
                PERMISSION_DENIED
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewDataBinding = MainFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        mActivity.setSupportActionBar(viewDataBinding.toolbar)
        setupUI()
        setupObserve()

        if (locationPermissionStatus != PERMISSION_GRANTED) {
            requestLocationPermission()
        } else {
            viewModel.fetchNextItems()
        }

        return viewDataBinding.root
    }

    private fun setupObserve() {
        viewModel.openDetailEvent.observe(viewLifecycleOwner, { store ->
            if (store == null) return@observe
            val fragmentManager = fragmentManager ?: return@observe
            DetailFragment.newInstance(fragmentManager, R.id.container, store.id, store.name)
        })

        viewModel.locationErrorEvent.observe(viewLifecycleOwner, { error ->
            if (!error) return@observe
            viewModel.notifyLocationPermissionDeniedStatus(locationPermissionStatus)
        })

        viewModel.checkLocationErrorEvent.observe(viewLifecycleOwner, { check ->
            if (!check) return@observe
            viewModel.checkLocationErrorEvent.postValue(false)
            checkLocationPermissionAndHandle()
        })
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
    }

    private fun setupUI() {
        val recyclerView = viewDataBinding.recyclerView
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = StoreAdapter(viewModel, viewLifecycleOwner)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            val result = grantResults.getOrNull(0) ?: return
            if (result == PackageManager.PERMISSION_GRANTED) {
                viewModel.fetchNextItems()
            } else {
                viewModel.locationErrorEvent.value = true
            }
        }
    }

    private fun checkLocationPermissionAndHandle() {
        when (locationPermissionStatus) {
            PERMISSION_GRANTED -> viewModel.fetchNextItems()
            PERMISSION_DENIED -> requestLocationPermission()
            PERMISSION_BLOCKED -> viewModel.notifyLocationPermissionDeniedStatus(PERMISSION_BLOCKED)
        }
    }
}