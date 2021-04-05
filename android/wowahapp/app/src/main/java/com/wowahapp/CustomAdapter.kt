package com.wowahapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide




class CustomAdapter(private val data: List<RecipeModel>, application: CustomApplication) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    private var recipeList: MutableList<RecipeModel> = data as MutableList<RecipeModel>
    private val application = application
    var callback: RecyclerviewCallbacks<RecipeModel>? = null
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(recipe: RecipeModel, index: Int){
            val recipeName = view.findViewById<TextView>(R.id.recipeName)
            val averageSalePrice = view.findViewById<TextView>(R.id.averageSalePrice)
            val salePrice = view.findViewById<TextView>(R.id.salePrice)
            val link = view.findViewById<TextView>(R.id.link)
            val removeButton = view.findViewById<ImageButton>(R.id.removeItem)
            val itemImage = view.findViewById<ImageView>(R.id.itemImage)
            val profitability = recipe.getProfitability()
            recipeName.text = recipe.getRecipeName()
            averageSalePrice.text = recipe.getAverageSalePrice()
            salePrice.text=recipe.getSalePrice()
            link.text=recipe.getLink()
            link.setTextColor(getColor(profitability))

            Glide.with(view)
                .load(recipe.getImageLink())
                .into(itemImage)
            removeButton.setOnClickListener{
                removeItem(index)
                UserDataService.unSubRecipe(application.getUserName(), recipeName.text.toString(), recipe.getRealmID().toString(), application.applicationContext)
            }
            view.setOnClickListener{
                callback?.onItemClick(it, adapterPosition, recipeList[adapterPosition])
            }
        }

    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        recipeList.sortByDescending { it.getProfitability() }
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
    fun getColor(diff: Float): Int {
        var dif = diff
        var red = 0
        var blue = 0
        var green = 0
        when {
            dif>=3.0f -> {
                green = 255
            }
            dif>=0.0f -> {
                dif /= 3.0f
                green=255
                red = ((255-255*dif).toInt())
                blue = ((255-255*dif).toInt())
            }
            dif >=-3.0f -> {
                dif = (-dif)/3.0f
                red=255
                green = ((255-255*dif).toInt())
                blue = ((255-255*dif).toInt())
            }
            else -> {
                red = 255
            }

        }
        return Color.argb(255,red,green, blue)
    }

}