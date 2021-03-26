package com.wowahapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomAdapterShopping(private val data: List<RecipeModel>) :
    RecyclerView.Adapter<CustomAdapterShopping.MyViewHolder>() {
    private var recipeList: MutableList<RecipeModel> = data as MutableList<RecipeModel>
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(recipe: RecipeModel, index: Int){
            val recipeName = view.findViewById<TextView>(R.id.recipeName)
            val averageSalePrice = view.findViewById<TextView>(R.id.averageSalePrice)
            val salePrice = view.findViewById<TextView>(R.id.salePrice)
            val link = view.findViewById<TextView>(R.id.link)
            val toggleButton = view.findViewById<CheckBox>(R.id.toggleItem)
            val itemImage = view.findViewById<ImageView>(R.id.itemImage)
            recipeName.text = recipe.getRecipeName()
            averageSalePrice.text = recipe.getAverageSalePrice()
            salePrice.text=recipe.getSalePrice()
            link.text=recipe.getLink()
            Glide.with(view)
                .load(recipe.getImageLink())
                .into(itemImage)
            toggleButton.setOnClickListener() {
                toggleSubscribe(recipe, toggleButton)
            }
        }

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(recipeList[position], position)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun addItem(item: RecipeModel){
        recipeList.add(item)
        notifyDataSetChanged()
    }

    fun setItems(items: MutableList<RecipeModel>){
        recipeList = items
        notifyDataSetChanged()
    }

    fun getRecipeList(): MutableList<RecipeModel> {
        return recipeList
    }

    fun toggleSubscribe(recipe: RecipeModel, button: CheckBox) {
        recipe.setIsSelected(button.isChecked)
    }

}