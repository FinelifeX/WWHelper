package ru.kpfu.itis.wwhelper.ui.advice

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_rv_advice.view.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.clothing.Thing


/*
*** Created by Bulat Murtazin on 01.09.2018 ***
*/

class AdviceRecyclerViewAdapter(private val suggestedItems: List<Thing>, val context: Context) : RecyclerView.Adapter<AdviceRecyclerViewAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_rv_advice, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = suggestedItems.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Glide.with(context)
                .load(suggestedItems[position].imgUrl)
                .into(holder.itemView.iv_thing_advice)
        holder.itemView.tv_thing_type_advice.text = suggestedItems[position].type.toString()
        holder.itemView.tv_thing_color_advice.text = suggestedItems[position].color
    }

}