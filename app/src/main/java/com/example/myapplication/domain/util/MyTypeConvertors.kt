package com.example.myapplication.domain.util

import androidx.room.TypeConverter
import com.example.myapplication.data.cache.IngredientCacheEntity
import com.example.myapplication.domain.model.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyTypeConvertors {

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromIngredientList(ingredient: List<IngredientCacheEntity>?): String? {
        if (ingredient == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<IngredientCacheEntity>?>() {}.type
        return gson.toJson(ingredient, type)
    }

    @TypeConverter
    fun toIngredientList(ingredient: String?): List<IngredientCacheEntity>? {
        if (ingredient == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<IngredientCacheEntity>?>() {}.type
        return gson.fromJson<List<IngredientCacheEntity>>(ingredient, type)
    }
}