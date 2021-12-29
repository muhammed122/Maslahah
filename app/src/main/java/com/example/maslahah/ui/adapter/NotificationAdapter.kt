package com.example.maslahah.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import java.util.ArrayList

class NotificationAdapter() :
    RecyclerView.Adapter<NotificationAdapter.Holder>() {

    var list = ArrayList<String>()


    fun addNotification(body: String) {
        list.add(0,body)
        notifyItemInserted(0)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_item_layout, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = list[position]
        holder.title.text = data
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var title: AppCompatTextView = view.findViewById(R.id.notification_desc)
    }

}