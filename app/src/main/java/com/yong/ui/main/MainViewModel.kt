package com.yong.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.VolleyError
import com.yong.R
import com.yong.data.api.ApiHelper
import com.yong.data.model.Store
import com.yong.data.model.StoreResponse
import com.yong.ui.ErrorViewModel
import com.yong.ui.LoadingViewModel

class MainViewModel(private val context: Context, private val apiHelper: ApiHelper) : ViewModel(),
    ErrorViewModel, LoadingViewModel, ApiHelper.ResponseListener<StoreResponse> {
    override val isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    override val showError: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    override val errorMessage: MutableLiveData<String> = MutableLiveData<String>()
    override val errorCTA: MutableLiveData<String> = MutableLiveData<String>()

    val stores: MutableLiveData<MutableList<Store>> = MutableLiveData<MutableList<Store>>().apply { value = ArrayList() }
    var offset: Int = 0
    val limit: Int = 50
    var hasMore: Boolean = false
    val title: MutableLiveData<String> = MutableLiveData<String>()
    val openDetailEvent: MutableLiveData<Store?> = MutableLiveData()
    var locationErrorEvent: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val checkLocationErrorEvent: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchNextItems() {
        showError.value = false
        locationErrorEvent.value = false
        isLoading.value = stores.value.isNullOrEmpty()
        apiHelper.fetchRestaurant(offset, limit, this)
    }

    override fun onSuccess(response: StoreResponse) {
        val value = stores.value ?: ArrayList()
        value.addAll(response.stores)
        hasMore = response.nextOffset != null
        offset = response.nextOffset ?: 0
        stores.value = value
        title.value = "${context.getString(R.string.discovery)} (${value.size})"
        isLoading.value = false
        showError.value = false
    }

    override fun onFailure(error: VolleyError) {
        setErrorMessage(error.message)
    }

    private fun setErrorMessage(message: String?) {
        isLoading.value = false
        showError.value = true
        errorMessage.value = message
        errorCTA.value = context.getString(R.string.error_cta_retry)
    }

    fun openDetail(store: Store) {
        openDetailEvent.value = store
    }

    fun notifyLocationPermissionDeniedStatus(status: Int) {
        isLoading.value = false
        showError.value = true
        if (status == PERMISSION_DENIED) {
            errorMessage.value = context.getString(R.string.error_msg_location_denied)
            errorCTA.value = context.getString(R.string.error_cta_location_denied)
        } else if (status == PERMISSION_BLOCKED) {
            errorMessage.value = context.getString(R.string.error_msg_location_blocked)
            errorCTA.value = context.getString(R.string.error_cta_location_blocked)
        } else {
            locationErrorEvent.value = false
            setErrorMessage(context.getString(R.string.error_msg_no_known_location))
        }
    }

    override fun errorCTAClick() {
        if (locationErrorEvent.value == true) {
            checkLocationErrorEvent.value = true
        } else {
            fetchNextItems()
        }

    }

    override fun onLocationFailure() {
        locationErrorEvent.value = true
    }
}