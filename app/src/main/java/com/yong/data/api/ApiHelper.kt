package com.yong.data.api

import GsonRequest
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.yong.data.model.AuthnResponse
import com.yong.data.model.DetailResponse
import com.yong.data.model.StoreResponse
import org.json.JSONObject


private const val STORE_API = "https://api.doordash.com/v1/store_feed/?lat=%s&lng=%s&offset=%d&limit=%d"
private const val DETAIL_API = "https://api.doordash.com/v2/restaurant/%d"
private const val AUTHN_API = "https://api.doordash.com/v2/auth/token/"
private const val PREF_AUTHN_TOKEN = "authnToken"

class ApiHelper(private val context: Application) {
    interface ResponseListener<T> {
        fun onSuccess(response: T)
        fun onFailure(error: VolleyError)
        fun onLocationFailure() {}
    }
    private val authnPref: SharedPreferences = context.getSharedPreferences(
        "authnPref",
        Context.MODE_PRIVATE
    )

    val isLoggedIn: Boolean
        get() {
            return !authnPref.getString(PREF_AUTHN_TOKEN, null).isNullOrEmpty()
        }

    private val favoritePref: SharedPreferences = context.getSharedPreferences(
        "favoritePref",
        Context.MODE_PRIVATE
    )

    fun logout() {
        authnPref.edit().remove(PREF_AUTHN_TOKEN).apply()
    }

    fun toggleFavorite(storeId: Long): Boolean {
        val isFavorite = isFavorite(storeId)
        favoritePref.edit().putBoolean(storeId.toString(), !isFavorite).apply()
        return !isFavorite
    }

    fun performAuthn(account: String, password: String, listener: ResponseListener<AuthnResponse>) {
        val queue = Volley.newRequestQueue(context)
        val url = AUTHN_API
        val request =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                val gson = Gson()
                val authnResponse = gson.fromJson(response, AuthnResponse::class.java)
                if (authnResponse.token.isNullOrEmpty()) {
                    listener.onFailure(VolleyError("no token provided"))
                } else {
                    authnPref.edit().putString(PREF_AUTHN_TOKEN, authnResponse.token).apply()
                    listener.onSuccess(authnResponse)
                }
            }, Response.ErrorListener { error ->
                listener.onFailure(error)
            }) {
                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val params2 = HashMap<String?, String?>()
                    params2["email"] = account
                    params2["password"] = password
                    return JSONObject(params2 as Map<*, *>).toString().toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
        queue.add(request)
    }

    fun isFavorite(storeId: Long) = favoritePref.getBoolean(storeId.toString(), false)

    fun fetchRestaurant(offset: Int, limit: Int, listener: ResponseListener<StoreResponse>) {
        val location = findLocation(offset, limit, listener)
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

    private fun findLocation(offset: Int, limit: Int, listener: ResponseListener<StoreResponse>): Pair<Double, Double>? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val provider = locationManager?.getBestProvider(Criteria(), true) ?: return null
        val location = locationManager.getLastKnownLocation(provider)
        if (location == null) {
            locationManager.requestLocationUpdates(provider, 1000L, 0F,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationManager.removeUpdates(this)
                            fetchRestaurant(offset, limit, listener)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }
                })
        }
        val latitude = location?.latitude
        val longitude = location?.longitude
        if (latitude == null || longitude == null) return null
        return Pair(location.latitude, location.longitude)
    }
}