package com.example.myapplication.data.network.model

import androidx.lifecycle.GeneratedAdapter
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.model.Ingredient
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class RecipeNetworkResponse(
    @SerializedName("_links")
    @Expose
    val _links: Links,

    @SerializedName("count")
    @Expose
    val count: Int,

    @SerializedName("from")
    @Expose
    val from: Int,

    @SerializedName("to")
    @Expose
    val to: Int,

    @SerializedName("hits")
    @Expose
    val hits: List<Hit>


)


data class Hit(
    @SerializedName("_links")
    @Expose
    val _links: LinksX,

    @SerializedName("recipe")
    @Expose
    val recipe: RecipeNetworkEntity
)

data class LinksX(
    @SerializedName("self")
    @Expose
    val self: Self
)

data class Self(
    @SerializedName("href")
    @Expose
    val href: String,

    @SerializedName("title")
    @Expose
    val title: String
)

data class RecipeNetworkEntity(
    @SerializedName("calories")
    @Expose
    val calories: Double,

    @SerializedName("cautions")
    @Expose
    val cautions: List<String>?,

    @SerializedName("cuisineType")
    @Expose
    val cuisineType: List<String>?,

    @SerializedName("dietLabels")
    @Expose
    val dietLabels: List<String>?,

    @SerializedName("dishType")
    @Expose
    val dishType: List<String>?,

    @SerializedName("healthLabels")
    @Expose
    val healthLabels: List<String>,

    @SerializedName("image")
    @Expose
    val image: String?,

    @SerializedName("ingredientLines")
    @Expose
    val ingredientLines: List<String>,

    @SerializedName("ingredients")
    @Expose
    val ingredients: List<IngredientNetworkEntity>?,

    @SerializedName("label")
    @Expose
    val label: String,

    @SerializedName("mealType")
    @Expose
    val mealType: List<String>?,

    @SerializedName("shareAs")
    @Expose
    val shareAs: String,

    @SerializedName("source")
    @Expose
    val source: String,

    @SerializedName("totalTime")
    @Expose
    val totalTime: Double?,

    @SerializedName("totalWeight")
    @Expose
    val totalWeight: Double,

    @SerializedName("uri")
    @Expose
    val uri: String?,

    @SerializedName("url")
    @Expose
    val url: String?,

    @SerializedName("yield")
    @Expose
    val yield: Double?
)


data class Links(
    @SerializedName("next")
    @Expose
    val next: Next
)

data class Next(
    @SerializedName("href")
    @Expose
    val href: String,

    @SerializedName("title")
    @Expose
    val title: String
)

data class IngredientNetworkEntity(
    @SerializedName("foodCategory")
    @Expose
    val foodCategory: String?,

    @SerializedName("foodId")
    @Expose
    val foodId: String,

    @SerializedName("image")
    @Expose
    val image: String?,

    @SerializedName("text")
    @Expose
    val text: String,

    @SerializedName("weight")
    @Expose
    val weight: Double
)