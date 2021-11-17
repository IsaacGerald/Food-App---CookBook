package com.example.myapplication.presentation.ui.recipe

sealed class RecipeStateEvent {

    data class getRecipe(val query: String): RecipeStateEvent()
}