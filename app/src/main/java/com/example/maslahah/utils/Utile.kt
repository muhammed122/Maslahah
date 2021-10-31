package com.example.maslahah.utils

import android.app.Activity
import android.content.Context

import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.maslahah.R
import com.google.android.material.snackbar.Snackbar


object Utile {

    fun showSucesSnakbar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
            .setAction("Ok") {
                // Responds to click on the action
            }
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.main_color))
            .setActionTextColor(ContextCompat.getColor(view.context, R.color.white))
            .show()

    }

//    fun isOnline(context: Context): Boolean {
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val capabilities =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//            } else {
//                TODO("VERSION.SDK_INT < M")
//            }
//        if (capabilities != null) {
//            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
//                return true
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
//                return true
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
//                return true
//            }
//        } else {
//            val activeNetworkInfo = connectivityManager.activeNetworkInfo
//            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
//                return true
//            }
//        }
//        return false
//    }

    fun hideKeyboard(activity: Activity) {
        val inputManager: InputMethodManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // check if no view has focus:
        val currentFocusedView = activity.currentFocus
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


    val URL = "http://34.122.206.57:8080/"
    val URLM = "http://34.122.206.57:8080/api/v1"

    val MEDIA_URL="https://dashboard.drovox.com/"
    val MEDIA_URL_FILES="https://dashboard.drovox.com/storage/"




}