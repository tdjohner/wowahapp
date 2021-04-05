package com.wowahapp

class RecipeHandle (recipeName: String?, thumbURL: String?) {
        private var recipeName: String
        private var thumbURL: String

        init{
            this.recipeName = recipeName!!
            this.thumbURL = thumbURL!!
        }

        public fun getURL(): String {
            return thumbURL
        }

        public fun getName(): String {
            return recipeName
        }

}