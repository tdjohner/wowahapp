package com.wowahapp

import android.widget.CheckBox

class RecipeModel (recipeName: String?, averageSalePrice: String?, salePrice: String?, link: String?, imageLink: String?, realmID: Int?){
    private var recipeName: String
    private var averageSalePrice: String
    private var salePrice: String
    private var link: String
    private var isSelected: Boolean
    private var imageLink: String
    private var realmID: Int
    init{
        this.recipeName = recipeName!!
        this.averageSalePrice = averageSalePrice!!
        this.salePrice = salePrice!!
        this.link = link!!
        this.isSelected = false
        this.imageLink = imageLink!!
        this.realmID = realmID!!
    }

    fun getRealmID(): Int?{
        return realmID
    }

    fun setRealmID(id: Int?){
        this.realmID = id!!
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
    fun getDiff(): Float{
        var diff: Float = 1.0F
        if(this.link.toFloat()!=0.0F){
            diff=(this.link.toFloat()-this.salePrice.toFloat())/this.link.toFloat()
            //diff=this.link.toFloat() //to test values directly
        }
        return diff
    }
}