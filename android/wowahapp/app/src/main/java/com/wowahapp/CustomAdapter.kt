package com.wowahapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val data: List<RecipeModel>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    private var recipeList: MutableList<RecipeModel> = data as MutableList<RecipeModel>
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(recipe: RecipeModel, index: Int){
            val recipeName = view.findViewById<TextView>(R.id.recipeName)
            val averageSalePrice = view.findViewById<TextView>(R.id.averageSalePrice)
            val salePrice = view.findViewById<TextView>(R.id.salePrice)
            val link = view.findViewById<TextView>(R.id.link)
            val removeButton = view.findViewById<Button>(R.id.removeItem)
            recipeName.text = recipe.getRecipeName()
            averageSalePrice.text = recipe.getAverageSalePrice()
            salePrice.text=recipe.getSalePrice()
            link.text=recipe.getLink()

            removeButton.setOnClickListener{removeItem(index)}
        }

    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(recipeList[position], position)
    }
    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun removeItem(index: Int){
        recipeList.removeAt(index)
        notifyDataSetChanged()
    }

    fun addItem(item: RecipeModel){
        recipeList.add(item)
        notifyDataSetChanged()
    }

    fun setItems(items: MutableList<RecipeModel>){
        recipeList = items
        notifyDataSetChanged()
    }

}
/*
internal class CustomAdapter(private var itemsList: MutableList<RecipeModel>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
        internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
            var recipeName: TextView = view.findViewById(R.id.recipeName)
            var averageSalePrice: TextView = view.findViewById(R.id.averageSalePrice)
            var salePrice: TextView = view.findViewById(R.id.salePrice)
            var link: TextView = view.findViewById(R.id.link)
            val removeButton: Button = view.findViewById<Button>(R.id.removeItem)
        }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.recipeName.text = item.getRecipeName()
        holder.averageSalePrice.text = item.getAverageSalePrice()
        holder.salePrice.text = item.getSalePrice()
        holder.link.text = item.getLink()
    }
    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun removeItem(index: Int){
        itemsList.removeAt(index)
        notifyDataSetChanged()
    }

}*/