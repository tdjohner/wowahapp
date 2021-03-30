package com.wowahapp

import android.view.View

interface RecyclerviewCallbacks<Item> {
    fun onItemClick(view: View, position: Int, item: Item)
}