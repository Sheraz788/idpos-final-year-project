package com.example.sherazali.idpos_app.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.Notifications
import kotlinx.android.synthetic.main.notifications_adapter.view.*

class NotificationsAdapter(var notifications: Notifications) : RecyclerView.Adapter<NotificationsCustomHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): NotificationsCustomHolder {


        var layoutInflater = LayoutInflater.from(parent.context)

        var notificationLayout = layoutInflater.inflate(R.layout.notifications_adapter, parent, false)

        return NotificationsCustomHolder(notificationLayout)
    }

    override fun getItemCount(): Int {

        return notifications.notifications.count()
    }

    override fun onBindViewHolder(holder: NotificationsCustomHolder, pos: Int) {

        var notification = notifications.notifications.get(pos)


        holder.view.notifyCustomerName.text = notification.CustomerName
        holder.view.notifyCustomerContact.text = notification.Contact
        holder.view.notifyProductName.text = notification.ProductName
        holder.view.notifyStoreName.text = notification.StoreName
        holder.view.notifyPQuantity.text = notification.Quantity.toString()
        holder.view.notifyPPrice.text = notification.Price.toString()




    }

}

class NotificationsCustomHolder(var view:View) : RecyclerView.ViewHolder(view)