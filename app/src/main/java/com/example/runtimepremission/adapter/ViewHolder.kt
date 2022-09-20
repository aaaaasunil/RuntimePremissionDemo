package com.example.runtimepremission.adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.runtimepremission.R
import com.example.runtimepremission.callBack.RecycleViewItemClick
import com.example.runtimepremission.model.CartItems
import java.lang.String
import kotlin.Boolean
import kotlin.Int

class ViewHolder(viewHolder: View) : RecyclerView.ViewHolder(viewHolder) {

    var tvCount: TextView? = null;
    var tvIncrease: TextView? = null;
    var tvDecrease: TextView? = null;
    var clRoots: ConstraintLayout? = null;

    fun bindView(itemModel: CartItems, clickListener: RecycleViewItemClick) {

        tvCount!!.setText("" + itemModel.quantity)
        setOnClickLstener(itemModel, clickListener)
    }

    private fun setOnClickLstener(itemModel: CartItems, clickListener: RecycleViewItemClick) {

        tvIncrease!!.setOnClickListener {
            var count: Int = String.valueOf(
                tvCount!!.getText()
            ).toInt()
            count++
            tvCount!!.setText("" + count)
        }

        tvDecrease!!.setOnClickListener {

            var count: Int = String.valueOf(tvCount!!.getText()).toInt()
            if (count == 1) {
                tvCount!!.setText("1")
            } else {
                count -= 1
                tvCount!!.setText("" + count)
            }

        }

        if (itemModel.isSelected)
            clRoots!!.setBackgroundColor(itemView.context.resources.getColor(R.color.colorMediumYellow))
        else
            clRoots!!.setBackgroundColor(itemView.context.resources.getColor(R.color.white))


        clRoots!!.setOnLongClickListener(object : View.OnLongClickListener {

            override fun onLongClick(v: View?): Boolean {

                    itemModel.isSelected = !itemModel.isSelected
                    clickListener.onRecycleViewClickListener(model = itemModel, position = adapterPosition)

                return true
            }
        })

    }

    init {
        tvCount = viewHolder.findViewById<TextView>(R.id.tvCount)
        tvDecrease = viewHolder.findViewById<TextView>(R.id.tvDecrease)
        tvIncrease = viewHolder.findViewById<TextView>(R.id.tvIncrease)
        clRoots = viewHolder.findViewById<ConstraintLayout>(R.id.clRoots)

    }

}