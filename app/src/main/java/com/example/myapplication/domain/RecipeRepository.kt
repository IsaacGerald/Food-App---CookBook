package com.example.myapplication.domain

import com.example.myapplication.data.cache.RecipeCacheEntity
import com.example.myapplication.data.cache.RecipeCacheMapper
import com.example.myapplication.data.network.ApiService
import com.example.myapplication.data.network.model.RecipeNetworkMapper
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.DataState
import com.example.myapplication.domain.util.EntityMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface RecipeRepository {

    fun getRecipes(queryString: String): Flow<DataState<List<Recipe>>>
    suspend fun getSearchRecipes(queryString: String): Flow<List<Recipe>?>
    suspend fun updateRecipes(recipe: Recipe)
    suspend fun getRecentlySearchedRecipe(isRecentRecipe: Boolean): Flow<List<Recipe>>
    suspend fun getFavoriteRecipes(isFavorite: Boolean): Flow<List<Recipe>?>



}