package com.example.myapplication.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.data.network.model.IngredientNetworkEntity
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.EntityMapper

@Entity(tableName = "recipe_table")
data class RecipeCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val calories: Double,
    val cautions: List<String>?,
    val cuisineType: List<String>?,
    val dietLabels: List<String>?,
    val dishType: List<String>?,
    val healthLabels: List<String>,
    val image: String?,
    val ingredientLines: List<String>,
    val ingredients: List<IngredientCacheEntity>?,
    val label: String,
    val type: String?,
    val mealType: List<String>?,
    val shareAs: String,
    val source: String,
    val totalTime: Double?,
    val totalWeight: Double,
    val uri: String?,
    val isFavorite: Boolean = false,
    val isRecentlyViewed: Boolean = false,
    val url: String?,
    val yield: Double?

)

data class IngredientCacheEntity(
    val foodCategory: String?,
    val foodId: String,
    val image: String?,
    val text: String,
    val weight: Double
)