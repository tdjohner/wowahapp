package com.wowahapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.*
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.math.absoluteValue


class CustomAdapter(private val data: List<RecipeModel>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    private var recipeList: MutableList<RecipeModel> = data as MutableList<RecipeModel>
    var callback: RecyclerviewCallbacks<RecipeModel>? = null
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(recipe: RecipeModel, index: Int){
            val recipeName = view.findViewById<TextView>(R.id.recipeName)
            val averageSalePrice = view.findViewById<TextView>(R.id.averageSalePrice)
            val salePrice = view.findViewById<TextView>(R.id.salePrice)
            val link = view.findViewById<TextView>(R.id.link)
            val removeButton = view.findViewById<Button>(R.id.removeItem)
            val itemImage = view.findViewById<ImageView>(R.id.itemImage)
            var diff = recipe.getDiff()
            recipeName.text = recipe.getRecipeName()
            averageSalePrice.text = recipe.getAverageSalePrice()
            salePrice.text=recipe.getSalePrice()
            link.text=recipe.getLink()
            var positive=Color.argb(255,0,255,0)
            val negative=Color.argb(255,255,0,0)
            var color: Int
            when {
                diff>=2.0f -> {
                    color= positive
                }
                diff>=-2.0f -> {
                    val blue = ((2.0f-diff.absoluteValue)*127).toInt()
                    diff=diff/2.0f+1.0f
                    color = (positive+((positive-negative)*diff)).toInt()
                    color += Color.argb(255,0,0,blue)
                }
                else -> {
                    color=negative
                }
            }
            link.setTextColor(color)
            Glide.with(view)
                .load(recipe.getImageLink())
                .into(itemImage)
            removeButton.setOnClickListener{removeItem(index)}
            view.setOnClickListener{
                callback?.onItemClick(it, adapterPosition,recipeList[adapterPosition])
            }
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

    fun setOnClick(click: RecyclerviewCallbacks<RecipeModel>) {
        callback=click
    }
}