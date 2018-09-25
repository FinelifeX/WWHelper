package ru.kpfu.itis.wwhelper.ui.clothing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_list_things.view.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.clothing.Thing
import ru.kpfu.itis.wwhelper.model.provider.UserProvider


/*
*** Created by Bulat Murtazin on 29.08.2018 ***
*/

class ThingListAdapter(private val list: MutableList<Thing>, val context: Context) : RecyclerView.Adapter<ThingListAdapter.ThingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_things, parent, false)
        return ThingViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ThingViewHolder, position: Int) {
        Glide.with(context)
                .load(list[position].imgUrl)
                .into(holder.itemView.iv_thing)
        holder.itemView.tv_thing_type.text = list[position].type
        holder.itemView.tv_thing_color.text = list[position].color
        holder.itemView.btn_thing_delete.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("things")
                    .child(UserProvider.getCurrentUser()!!.uid!!)
                    .child(list[position].uid!!).removeValue()
            list.removeAt(position)
            notifyDataSetChanged()
        }
    }

    class ThingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}