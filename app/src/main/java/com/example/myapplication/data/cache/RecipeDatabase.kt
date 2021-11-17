package com.example.myapplication.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.myapplication.domain.util.MyTypeConvertors

@Database(entities = [RecipeCacheEntity::class], version = 7 ,exportSchema = false)
@TypeConverters(MyTypeConvertors::class)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao



}