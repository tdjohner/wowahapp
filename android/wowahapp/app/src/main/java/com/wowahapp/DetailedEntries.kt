package com.wowahapp

class DetailedEntries(message:ArrayList<String>, header:Boolean=false) {
    private var type: Int
    private var message: ArrayList<String> = message
    private var header: Boolean = header
    init{
        this.type=this.message.size
    }
    fun getType(): Int{
        return this.type
    }
    fun isHeader(): Boolean{
        return this.header
    }
    fun getMessage(): ArrayList<String>{
        return this.message
    }
}