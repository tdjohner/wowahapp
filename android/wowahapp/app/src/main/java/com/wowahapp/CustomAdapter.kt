package com.wowahapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.*
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
            val removeButton = view.findViewById<ImageButton>(R.id.removeItem)
            val itemImage = view.findViewById<ImageView>(R.id.itemImage)
            var diff = recipe.getDiff()
            recipeName.text = recipe.getRecipeName()
            averageSalePrice.text = recipe.getAverageSalePrice()
            salePrice.text=recipe.getSalePrice()
            link.text=recipe.getLink()
            var positive=Color.argb(255,0,255,0)
            val negative=Color.argb(255,255,0,0)
            val neutral=Color.argb(255,255,255, 255)
            var color: Int
            /*when {
                diff>=3.0f -> {
                    color= positive
                }
                diff>=2.0f -> {
                    diff -= 2.0f
                    color = (positive/2+((positive-positive/2)*diff)).toInt()
                }
                diff>=1.0f -> {
                    diff-=1.0f
                    color = (neutral+((positive/2-neutral)*diff)).toInt()
                }
                diff>=0.5f -> {
                    diff=diff*2.0f-1.0f
                    color = (negative/2+((neutral-negative/2)*diff)).toInt()
                }
                diff>=0.333f ->{
                    diff=diff*6.0f-2.0f
                    color = (negative+((negative/2-negative)*diff)).toInt()
                }
                else -> {
                    color=negative
                }
            }*/
            when {
                diff>=3.0f -> {
                    color= positive
                }
                diff>=2.0f -> {
                    diff -= 2.0f
                    color = (positive/1.5+((positive-positive/1.5)*diff)).toInt()
                }
                diff>=0.0f -> {
                    diff/=2.0f
                    color = (neutral+((positive/1.5-neutral)*diff)).toInt()
                }
                diff>=-2.0f -> {
                    diff= diff/2.0f +1.0f
                    color = (neutral+((negative/1.5-neutral)*diff)).toInt()
                }
                diff>=-3.0f ->{
                    diff+=3.0f
                    color = (negative+((negative/1.5-negative)*diff)).toInt()
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