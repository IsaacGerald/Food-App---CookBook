package com.example.myapplication.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Recipe(
    val id: Int,
    val calories: Double,
    val cautions: List<String>?,
    val cuisineType: List<String>?,
    val dietLabels: List<String>?,
    val dishType: List<String>?,
    val healthLabels: List<String>,
    val image: String?,
    val ingredientLines: List<String>,
    val ingredients: @RawValue List<Ingredient>?,
    val label: String,
    val type: String? = null,
    val mealType: List<String>?,
    val shareAs: String,
    val source: String,
    val totalTime: Double?,
    val totalWeight: Double,
    val uri: String?,
    val url: String?,
    val isFavorite: Boolean = false,
    val isRecentlyViewed: Boolean = false,
    val yield: Double?
): Parcelable

data class Ingredient(
    val foodCategory: String?,
    val foodId: String,
    val image: String?,
    val text: String,
    val weight: Double
)