package com.yong.ui.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.volley.VolleyError
import com.yong.R
import com.yong.data.api.ApiHelper
import com.yong.data.model.Store
import com.yong.data.model.StoreResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var context: Context

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        mainViewModel = MainViewModel(context, apiHelper)
        `when`(context.getString(eq(R.string.discovery))).thenReturn("Discovery")
        `when`(context.getString(eq(R.string.error_cta_retry))).thenReturn("Retry")
    }

    @Test
    fun testLoadingSpinnerVisibleOnlyEmptyStore() {
        whenFetchNextItems()
        verifyLoadingVisible(true)

        whenFetchItemsSuccess(22, 5, 5)
        whenFetchNextItems()
        verifyLoadingVisible(false)
        verifyTitle("${context.getString(R.string.discovery)} (5)")

        whenFetchItemsSuccess(22, 5, 5)
        whenFetchNextItems()
        verifyLoadingVisible(false)
        verifyTitle("${context.getString(R.string.discovery)} (10)")
    }

    @Test
    fun testHasMore() {
        whenFetchNextItems()
        verifyHasMore(false)

        whenFetchItemsSuccess(12, 5, 5)
        verifyHasMore(true)
        verifyTitle("${context.getString(R.string.discovery)} (5)")

        whenFetchItemsSuccess(12, 5, 5)
        verifyHasMore(true)
        verifyTitle("${context.getString(R.string.discovery)} (10)")

        whenFetchItemsSuccess(12, null, 2)
        verifyHasMore(false)
        verifyTitle("${context.getString(R.string.discovery)} (12)")
    }

    @Test
    fun testLocationPermission() {
        whenLocationPermissionStatus(PERMISSION_DENIED)
        verifyErrorMessagesForLocationPermission(PERMISSION_DENIED)

        whenLocationPermissionStatus(PERMISSION_BLOCKED)
        verifyErrorMessagesForLocationPermission(PERMISSION_BLOCKED)
    }

    @Test
    fun testErrorCTA() {
        whenFetchNextItems()
        whenErrorFromStoreResponse()
        verifyErrorVisible()
        whenErrorCtaClick()
        verifyFetchRestaurant(true)

        whenLocationPermissionStatus(PERMISSION_DENIED)
        whenErrorCtaClick()
        verifyFetchRestaurant(false)
    }

    private fun verifyErrorVisible() {
        assertEquals(true, mainViewModel.showError.value)
        assertNotNull(mainViewModel.errorMessage.value)
        assertNotNull(mainViewModel.errorCTA.value)
    }

    private fun whenErrorFromStoreResponse() {
        mainViewModel.onFailure(VolleyError("testError"))
    }

    private fun whenErrorCtaClick() {
        reset(apiHelper)
        mainViewModel.errorCTAClick()
    }

    private fun verifyFetchRestaurant(expected: Boolean) {
        if (expected) {
            verify(apiHelper, times(1)).fetchRestaurant(anyInt(), anyInt(), any(mainViewModel.javaClass))
        } else {
            verify(apiHelper, never()).fetchRestaurant(anyInt(), anyInt(), any(mainViewModel.javaClass))
        }

    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    private fun verifyErrorMessagesForLocationPermission(status: Int) {
        assertEquals(true, mainViewModel.showError.value)
        if (status == PERMISSION_DENIED) {
            assertEquals(context.getString(R.string.error_msg_location_denied), mainViewModel.errorMessage.value)
            assertEquals(context.getString(R.string.error_cta_location_denied), mainViewModel.errorCTA.value)
        } else {
            assertEquals(context.getString(R.string.error_msg_location_blocked), mainViewModel.errorMessage.value)
            assertEquals(context.getString(R.string.error_cta_location_blocked), mainViewModel.errorCTA.value)
        }
    }

    private fun whenLocationPermissionStatus(status: Int) {
        mainViewModel.locationErrorEvent.value = true
        mainViewModel.notifyLocationPermissionDeniedStatus(status)
    }

    private fun verifyTitle(expected: String) {
        assertEquals(expected, mainViewModel.title.value)
    }

    private fun verifyLoadingVisible(expected: Boolean) {
        assertEquals(expected, mainViewModel.isLoading.value)
    }

    private fun whenFetchNextItems() {
        mainViewModel.fetchNextItems()
    }

    // mock api response data
    private fun whenFetchItemsSuccess(results: Int, offset: Int?, numOfStores: Int) {
        val stores = ArrayList<Store>()
        for (i in 0 until numOfStores) {
            stores.add(Store(i.toLong(), "name $i", "description $i", "imageurl_$i"))
        }

        mainViewModel.onSuccess(StoreResponse(results = results, nextOffset = offset, stores = stores))
    }

    private fun verifyHasMore(expected: Boolean) {
        assertEquals(expected, mainViewModel.hasMore)
    }

}
