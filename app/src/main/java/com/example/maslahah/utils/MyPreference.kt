package com.example.maslahah.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.maslahah.data.UserData

import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


object MyPreference {
    private var appContext: Context? = null
    private val SHARED_PREFERENCES_NAME = "user data"


    private const val NOTIFICATION_ID = "notification_id"


    fun setNotificationId(id: Long) {
        getSharedPreferences()?.edit()
            ?.putLong(NOTIFICATION_ID, id)
            ?.apply()
    }

    fun getNotificationId(): Long {
        return getSharedPreferences()?.getLong(NOTIFICATION_ID, 0L)!!
    }


    //methods

    //methods
    fun init(context: Context?) {
        appContext = context
    }

    private fun getSharedPreferences(): SharedPreferences? {
        return appContext?.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    fun saveUserData(user: UserData) {
        getSharedPreferences()?.edit()?.apply {
            putString("userName", user.name)
            putString("userPhone", user.phone)
            putString("userEmail", user.email)
            putString("userImage", user.image)
            putString("userToken", user.userToken)
            putInt("userTasks", user.tasks!!)
            putBoolean("available", user.available!!)
            putFloat("userBalance", user.balance?.toFloat()!!)
            putInt("userTax", user.tax?.toInt()!!)
            putString("userStatus", user.status!!)
        }?.apply()

    }

    fun getUserData(): UserData {
        return UserData(
            name = getSharedPreferences()?.getString("userName", "")!!,
            phone = getSharedPreferences()?.getString("userPhone", "")!!,
            email = getSharedPreferences()?.getString("userEmail", "")!!,
            tasks = getSharedPreferences()?.getInt("userTasks", 0)!!,
            balance = getSharedPreferences()?.getFloat("userBalance", 0.0F)!!.toDouble(),
            tax = getSharedPreferences()?.getInt("userTax", 0),
            image = getSharedPreferences()?.getString("userImage", "")!!,
            available = getSharedPreferences()?.getBoolean("available", false),
            status = getSharedPreferences()?.getString("userStatus", "true")!!,
        )

    }


    fun getPrefString(key: String): String {
        return getSharedPreferences()?.getString(key, "")!!
    }

    fun getPrefInt(key: String): Int {
        return getSharedPreferences()?.getInt(key, 0)!!
    }


    fun setPrefInt(key: String, value: Int) {
        getSharedPreferences()?.edit()?.putInt(key, value)?.apply()
    }

    fun setLanguage(value: String) {
        getSharedPreferences()?.edit()?.putString("lang", value)?.apply()
    }

    fun getLanguage(): String {
        return getSharedPreferences()?.getString("lang", "ar")!!

    }

    fun setPrefBoolean(key: String, value: Boolean) {
        getSharedPreferences()?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getPrefBoolean(key: String): Boolean {
        return getSharedPreferences()?.getBoolean(key, false)!!

    }

    fun savePrefFloat(key: String, value: Float) {
        getSharedPreferences()?.edit()?.putFloat(key, value)?.apply()
    }


    fun saveStringPreference(
        key: String?,
        value: String?
    ) {
        val editor = getSharedPreferences()?.edit()
        editor!!.putString(key, value)
        editor.apply()
    }

    fun saveIntPreference(
        key: String?,
        value: Int
    ) {
        val editor = getSharedPreferences()?.edit()
        editor!!.putInt(key, value)
        editor.apply()
    }




    fun clearData() {
        getSharedPreferences()?.edit()?.clear()?.apply()
    }


}