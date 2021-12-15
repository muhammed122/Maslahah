package com.example.maslahah.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maslahah.R
import com.example.maslahah.data.ServiceData
import java.util.ArrayList

class ServiceAdapter(private val serviceItemClickListener: ServiceItemClickListener) :
    RecyclerView.Adapter<ServiceAdapter.Holder>() {

    var list = ArrayList<ServiceData>()


    fun setOneService(serviceData: ServiceData) {
        list.add(0,serviceData)
        notifyItemInserted(0)

    }

    fun setServices(list: ArrayList<ServiceData>) {
        this.list = list
        notifyDataSetChanged()

    }


    fun updateService(service: ServiceData) {
        list.remove(
            list.find {
                it.id == service.id
            }
        ).also {
            if (it) {
                list.add(service)
                notifyDataSetChanged()
            }
        }
    }

    fun removeService(service: ServiceData) {
        list.remove(
            list.find {
                it.id == service.id
            }
        ).also {
            notifyDataSetChanged()
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.service_item_layout, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = list?.get(position)
        if (data != null) {
            holder.date.text = "${data.date}, ${data.time}"
            holder.price.text = "${data.price}  LE"
            holder.title.text = data.title
        }

        holder.itemView.setOnClickListener {
            serviceItemClickListener.serviceClickListener(data!!)
        }


    }

    override fun getItemCount(): Int {
        return if (list == null) {
            0
        } else {
            list?.size!!
        }
    }


    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {


        var date: AppCompatTextView = view.findViewById(R.id.service_time)
        var title: AppCompatTextView = view.findViewById(R.id.service_desc)
        var price: AppCompatTextView = view.findViewById(R.id.price)


    }

}