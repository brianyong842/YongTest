package com.yong.ui.detail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.VolleyError
import com.yong.R
import com.yong.data.api.ApiHelper
import com.yong.data.model.DetailResponse
import com.yong.ui.ErrorViewModel
import com.yong.ui.LoadingViewModel

class DetailViewModel(private val context: Context, private val apiHelper: ApiHelper) : ViewModel(),
    ErrorViewModel, LoadingViewModel, ApiHelper.ResponseListener<DetailResponse> {
    override val isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    override val showError: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    override val errorMessage: MutableLiveData<String> = MutableLiveData<String>()
    override val errorCTA: MutableLiveData<String> = MutableLiveData<String>()


    val image: MutableLiveData<String> = MutableLiveData<String>()
    val name: MutableLiveData<String> = MutableLiveData<String>()
    val description: MutableLiveData<String> = MutableLiveData<String>()
    val phone: MutableLiveData<String> = MutableLiveData<String>()

    private lateinit var details: DetailResponse
    var storeId: Long = 0L

    fun fetchDetails() {
        isLoading.value = true
        apiHelper.fetchDetail(storeId, this)
    }

    override fun onSuccess(response: DetailResponse) {
        details = response
        name.value = details.name
        image.value = details.image
        description.value = details.description
        phone.value = details.phone
        isLoading.value = false
    }

    override fun onFailure(error: VolleyError) {
        isLoading.value = false
        showError.value = true
        errorMessage.value = error.message
        errorCTA.value = context.getString(R.string.error_cta_retry)
    }

    override fun errorCTAClick() {
        showError.value = false
        errorMessage.value = null
        fetchDetails()
    }
}