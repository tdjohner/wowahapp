package com.wowahapp

class RecipeModel (recipeName: String?, averageSalePrice: String?, salePrice: String?, link: String?){
    private var recipeName: String
    private var averageSalePrice: String
    private var salePrice: String
    private var link: String
    init{
        this.recipeName = recipeName!!
        this.averageSalePrice = averageSalePrice!!
        this.salePrice = salePrice!!
        this.link = link!!
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
}