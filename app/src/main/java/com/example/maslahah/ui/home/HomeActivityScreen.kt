package com.example.maslahah.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.maslahah.R
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging


class HomeActivityScreen : LocalizationActivity(){

    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        ProgressLoading.init(this)
        MyPreference.setPrefBoolean("login", true)
        databaseReference = FirebaseDatabase.getInstance().reference
        getDeviceToken()
    }


    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("ddddddddd", "getDeviceToken: $token ")
            MyPreference.saveStringPreference("userToken", token)
            val phone = MyPreference.getPrefString("userPhone")
            databaseReference.child("users").child(phone).child("userToken").setValue(token)

        }


    }
}