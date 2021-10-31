package com.example.maslahah.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maslahah.R
import com.example.maslahah.ui.adapter.NotificationAdapter
import com.example.maslahah.ui.adapter.ServiceAdapter
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading
import com.google.firebase.database.*

class NotificationActivityScreen : AppCompatActivity() {

    lateinit var notificationAdapter: NotificationAdapter
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var recyclerView: RecyclerView


    lateinit var databaseReference: DatabaseReference

    private fun initRecyclerView() {
        notificationAdapter = NotificationAdapter()
        mLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.notification_recycler)
        recyclerView.apply {
            adapter = notificationAdapter
            layoutManager = mLayoutManager
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_screen)
        databaseReference = FirebaseDatabase.getInstance().reference
        initRecyclerView()
        getAllNotifications()


    }


    private fun getAllNotifications() {
        ProgressLoading.show(this)
        val phone = MyPreference.getPrefString("userPhone")
        databaseReference.child("notification").child(phone)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists())
                        notificationAdapter.addNotification(snapshot.getValue(String::class.java)!!)
                    ProgressLoading.dismiss()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@NotificationActivityScreen,
                        error.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    ProgressLoading.dismiss()
                }
            })
        ProgressLoading.dismiss()


    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivityScreen::class.java))
        finish()

    }
}