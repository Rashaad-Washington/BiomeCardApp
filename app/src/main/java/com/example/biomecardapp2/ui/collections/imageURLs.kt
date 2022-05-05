package com.example.biomecardapp2.ui.collections

object imageURLs{

    private var displayStringList: MutableList<String> = mutableListOf()

    fun addtoList(url : String){
        displayStringList.add(url)
    }

    fun getList(): MutableList<String> {
        return displayStringList
    }
}