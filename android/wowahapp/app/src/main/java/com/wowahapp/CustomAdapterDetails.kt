package com.wowahapp

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class CustomAdapterDetails(private val data: List<DetailedEntries>) :
    RecyclerView.Adapter<CustomAdapterDetails.MyViewHolder>() {
    private var reagentList: MutableList<DetailedEntries> = data as MutableList<DetailedEntries>
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(entry: DetailedEntries, index: Int){
            val first = view.findViewById<TextView>(R.id.firstSpace)
            val second = view.findViewById<TextView>(R.id.secondSpace)
            val third = view.findViewById<TextView>(R.id.thirdSpace)
            val message = entry.getMessage()
            if (entry.isHeader()){
                first.setTypeface(null, Typeface.BOLD)
                second.setTypeface(null, Typeface.BOLD)
                third.setTypeface(null, Typeface.BOLD)
            }
            first.text=message[0].toString()
            when (message.size) {
                2 -> {
                    third.text=message[1].toString()
                }
                3 -> {
                    second.text=message[1].toString()
                    third.text=message[2].toString()
                }
            }
        }

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(reagentList[position], position)
    }

    override fun getItemCount(): Int {
        return reagentList.size
    }

    fun addItem(item: DetailedEntries){
        reagentList.add(item)
        notifyDataSetChanged()
    }

    fun setItems(items: MutableList<DetailedEntries>){
        reagentList = items
        notifyDataSetChanged()
    }

    fun getRecipeList(): MutableList<DetailedEntries> {
        return reagentList
    }


}