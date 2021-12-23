package com.wowahapp



class DetailedView(title: String, reagents: ArrayList<ArrayList<String>>, available: ArrayList<ArrayList<String>>) {
    private var title: String = title
    private var reagents: ArrayList<ArrayList<String>> = reagents
    private var available: ArrayList<ArrayList<String>> = available
    fun getTitle(): String{
        return this.title
    }
    fun getEntries(): ArrayList<DetailedEntries>{
        val entryList: ArrayList<DetailedEntries> = ArrayList()
        this.reagents.sortBy { it[0] }
        this.available.sortBy{ it[2] }
        entryList.add(DetailedEntries(arrayListOf<String>("Reagents"), true))
        for (member in this.reagents){
            entryList.add(DetailedEntries(member))
        }
        entryList.add(DetailedEntries(arrayListOf<String>("Reagent Name","Available", "Cost"),true))
        for (member in this.available){
            entryList.add(DetailedEntries(member))
        }
        return entryList
    }

    fun addReagent(name: String, amount: String){
        val toAdd: ArrayList<String> = ArrayList()
        toAdd.add(name)
        toAdd.add(amount)
        this.reagents.add(toAdd)
    }

    fun addAvailable(available: Int, name: String, cost: String){
        val toAdd: ArrayList<String> = ArrayList()
        toAdd.add(available.toString())
        toAdd.add(name)
        toAdd.add(cost)
        this.available.add(toAdd)
    }
}