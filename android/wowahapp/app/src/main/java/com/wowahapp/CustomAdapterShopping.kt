package com.wowahapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.math.absoluteValue

class CustomAdapterShopping(private val data: List<RecipeModel>) :
    RecyclerView.Adapter<CustomAdapterShopping.MyViewHolder>(), Filterable {
    private var recipeList: MutableList<RecipeModel> = data as MutableList<RecipeModel>
    private var recipeListFull = deepCopyRecipeList(getDataList())
    private var safeCopyFlag = 0

    var callback: RecyclerviewCallbacks<RecipeModel>? = null
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(recipe: RecipeModel, index: Int){
            val recipeName = view.findViewById<TextView>(R.id.recipeName)
            val averageSalePrice = view.findViewById<TextView>(R.id.averageSalePrice)
            val salePrice = view.findViewById<TextView>(R.id.salePrice)
            val link = view.findViewById<TextView>(R.id.link)
            val toggleButton = view.findViewById<CheckBox>(R.id.toggleItem)
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

            toggleButton.setOnClickListener() {
                toggleSubscribe(recipe, toggleButton)
            }
            view.setOnClickListener{
                callback?.onItemClick(it, adapterPosition,recipeList[adapterPosition])
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (safeCopyFlag == 0) {
                    safeCopyFlag = 1
                    recipeListFull = deepCopyRecipeList(getDataList())
                }
                val charSearch = p0.toString().toLowerCase()
                var filterList = mutableListOf<RecipeModel>()
                if (charSearch.isEmpty()) {
                    filterList = deepCopyRecipeList(recipeListFull)
                } else {
                    for (row in recipeListFull) {
                        if (row.getRecipeName()?.toLowerCase()?.contains(charSearch)!!) {
                            filterList.add(row.deepCopy())
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                recipeList.clear()
                recipeList.addAll(results?.values as MutableList<RecipeModel>)
                notifyDataSetChanged()
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        recipeList.sortByDescending { it.getProfitability() }
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_item, parent, false)
        return MyViewHolder(itemView)
    }

    fun getDataList(): MutableList<RecipeModel> {
        return this.recipeList
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

    fun deepCopyRecipeList(rList: MutableList<RecipeModel>): MutableList<RecipeModel> {
        var newList = mutableListOf<RecipeModel>()

        for (r in rList) {
            val copy = r.deepCopy()
            newList.add(r.deepCopy())
        }
        return newList
    }
}