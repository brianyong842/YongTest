package com.yong.data.api

import GsonRequest
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.yong.data.model.DetailResponse
import com.yong.data.model.StoreResponse

private const val STORE_API = "https://api.doordash.com/v1/store_feed/?lat=%s&lng=%s&offset=%d&limit=%d"
private const val DETAIL_API = "https://api.doordash.com/v2/restaurant/%d"
class ApiHelper(private val context: Application) {
    interface ResponseListener<T> {
        fun onSuccess(response: T)
        fun onFailure(error: VolleyError)
        fun onLocationFailure() {}
    }

    fun fetchRestaurant(offset: Int, limit: Int, listener: ResponseListener<StoreResponse>) {
        val location = location
        if (location == null) {
            listener.onLocationFailure()
            return
        }

        val lat = location.first
        val long = location.second
        val queue = Volley.newRequestQueue(context)
        val url = String.format(STORE_API, lat, long, offset, limit)
        val request = GsonRequest(url, StoreResponse::class.java, { response ->
            listener.onSuccess(response)
        }, { error ->
            listener.onFailure(error)
        })
        queue.add(request)
    }

    fun fetchDetail(id: Long, listener: ResponseListener<DetailResponse>) {
        val queue = Volley.newRequestQueue(context)
        val url = String.format(DETAIL_API, id)
        val request = GsonRequest(url, DetailResponse::class.java, { response ->
            listener.onSuccess(response)
        }, { error ->
            listener.onFailure(error)
        })
        queue.add(request)
    }

    private val location: Pair<Double, Double>?
        get() {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null
            }
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val latitude = location?.latitude
            val longitude = location?.longitude
            if (latitude == null || longitude == null) return null
            return Pair(location.latitude, location.longitude)
        }

}