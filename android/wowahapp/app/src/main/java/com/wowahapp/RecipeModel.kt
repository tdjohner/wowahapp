package com.wowahapp

import android.widget.CheckBox

class RecipeModel (recipeName: String?, averageSalePrice: String?, salePrice: String?, link: String?, imageLink: String?, realmID: Int?){
    private var recipeName: String
    private var averageSalePrice: String
    private var salePrice: String
    private var link: String
    private var isSelected: Boolean
    private var imageLink: String
    private var profitability: Float
    private var realmID: Int
    init{
        this.recipeName = recipeName!!
        this.averageSalePrice = averageSalePrice!!
        this.salePrice = salePrice!!
        this.link = link!!
        this.isSelected = false
        this.imageLink = imageLink!!
        this.realmID = realmID!!
        this.profitability=this.getDiff()
    }

    fun deepCopy(): RecipeModel{
        return RecipeModel(this.recipeName, this.averageSalePrice, this.salePrice, this.link, this.imageLink, this.realmID)
    }

    fun getRealmID(): Int?{
        return realmID
    }

    fun setRealmID(id: Int?){
        this.realmID = id!!
        this.profitability=this.getDiff()
    }

    fun getRecipeName(): String?{
        return recipeName
    }
    fun setRecipeName(name: String?){
        this.recipeName = name!!
    }
    fun getAverageSalePrice(): String?{
        return averageSalePrice
    }
    fun setAverageSalePrice(averageSalePrice: String?){
        this.averageSalePrice = averageSalePrice!!
    }
    fun getSalePrice(): String?{
        return salePrice
    }
    fun setSalePrice(salePrice: String?){
        this.salePrice = salePrice!!
    }
    fun getLink(): String?{
        return link
    }
    fun setLink(link: String?){
        this.link = link!!
    }
    fun getIsSelected(): Boolean?{
        return isSelected
    }
    fun setIsSelected(link: Boolean?){
        this.isSelected = link!!
    }
    fun getImageLink(): String?{
        return imageLink
    }
    fun setImageLink(imageLink: String?){
        this.imageLink = imageLink!!
    }
    fun getProfitability(): Float{
        return profitability
    }
    fun setProfitability(){
        this.profitability=this.getDiff()
    }
    fun getDiff(): Float{
        var diff: Float = 0.0F
        if(this.salePrice.toFloat()!=0.0F){
            diff=(this.link.toFloat()-this.salePrice.toFloat())/this.salePrice.toFloat()
        }
        return diff
    }
}