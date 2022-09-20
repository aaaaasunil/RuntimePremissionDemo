package com.example.runtimepremission.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runtimepremission.R
import com.example.runtimepremission.callBack.RecycleViewItemClick
import com.example.runtimepremission.model.CartItems

class AddCartAdapter(context: Context,val cartList: ArrayList<CartItems>,var clickListener: RecycleViewItemClick) : RecyclerView.Adapter<ViewHolder>() {
    val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder=
        ViewHolder(layoutInflater.inflate(R.layout.row_cart_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bindView(cartList[position],clickListener)

    }

    override fun getItemCount(): Int = cartList.size
}