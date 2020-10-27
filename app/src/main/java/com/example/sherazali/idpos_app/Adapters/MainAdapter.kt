//package com.example.sherazali.idpos_app
//
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import kotlinx.android.synthetic.main.transactionadapter.view.*
//
//class MainAdapter : RecyclerView.Adapter<CustomViewHolder>() {
//
//    val serials = listOf("1","2", "3")
//
//    override fun getItemCount(): Int {
//        return serials.size
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//
//        val inflater = LayoutInflater.from(parent.context)
//        val row = inflater.inflate(R.layout.transactionadapter, parent, false)
//
//        return CustomViewHolder(row)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//
//        var serialNOs = serials.get(position)
//        holder.v.s_no_value.text = serialNOs
//
//    }
//
//
//
//}
//class CustomViewHolder(var v: View) : RecyclerView.ViewHolder(v){
//
//}
