package com.yong.ui.detail

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.volley.VolleyError
import com.yong.R
import com.yong.data.api.ApiHelper
import com.yong.data.model.Business
import com.yong.data.model.DetailResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {
    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var context: Context

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        viewModel = DetailViewModel(context, apiHelper)
        `when`(context.getString(ArgumentMatchers.eq(R.string.error_cta_retry))).thenReturn("Retry")
    }

    @Test
    fun testFetchDetailsSuccess() {
        whenFetchDetails()
        verifyLoadingVisible(true)

        whenFetchDetailsSuccess()
        verifyDetailsData()
    }

    @Test
    fun testFetchDetailsFailure() {
        whenFetchDetails()
        verifyLoadingVisible(true)

        whenFetchDetailsFailure()
        verifyErrorData()
    }

    @Test
    fun testErrorCTA() {
        whenFetchDetailsFailure()
        verifyErrorData()

        whenErrorCtaClick()
        verifyFetchDetails()
    }

    private fun verifyFetchDetails() {
        verify(apiHelper, times(1)).fetchDetail(anyLong(), any(viewModel.javaClass))
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    private fun whenErrorCtaClick() {
        reset(apiHelper)
        viewModel.errorCTAClick()
    }

    private fun verifyErrorData() {
        assertEquals(true, viewModel.showError.value)
        verifyLoadingVisible(false)
        assertNotNull(viewModel.errorMessage.value)
        assertNotNull(viewModel.errorCTA.value)
    }

    private fun whenFetchDetailsFailure() {
        viewModel.onFailure(VolleyError("testError"))
    }

    private fun verifyDetailsData() {
        verifyLoadingVisible(false)
        assertNotNull(viewModel.image.value)
        assertNotNull(viewModel.name.value)
        assertNotNull(viewModel.description.value)
        assertNotNull(viewModel.phone.value)
    }

    private fun whenFetchDetailsSuccess() {
        val business = Business("test")
        val tags = arrayListOf("tag1", "tag2")
        viewModel.onSuccess(DetailResponse(business, "phone", "image", "status", "description", tags))
    }

    private fun whenFetchDetails() {
        viewModel.fetchDetails()
    }

    private fun verifyLoadingVisible(expected: Boolean) {
        assertEquals(expected, viewModel.isLoading.value)
    }
}