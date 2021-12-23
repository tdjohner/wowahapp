package com.wowahapp

class RecipeHandle (recipeName: String?, thumbURL: String?, tierID: String?) {
        private var recipeName: String
        private var thumbURL: String
        private var tierID: String

        init{
            this.recipeName = recipeName!!
            this.thumbURL = thumbURL!!
            this.tierID = tierID!!
        }

        public fun getURL(): String {
            return thumbURL
        }

        public fun getName(): String {
            return recipeName
        }

        public fun getTier(): String {
            return tierID
        }

}