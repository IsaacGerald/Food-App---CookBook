package com.example.myapplication.domain

import android.util.Log
import androidx.room.withTransaction
import com.example.myapplication.data.cache.RecipeCacheEntity
import com.example.myapplication.data.cache.RecipeCacheMapper
import com.example.myapplication.data.cache.RecipeDatabase
import com.example.myapplication.data.network.ApiService
import com.example.myapplication.data.network.model.RecipeNetworkEntity
import com.example.myapplication.data.network.model.RecipeNetworkMapper
import com.example.myapplication.data.network.model.RecipeNetworkResponse
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl
@Inject constructor(
    private val db: RecipeDatabase,
    private val apiService: ApiService,
    private val cacheMapper: RecipeCacheMapper,
    private val networkMapper: RecipeNetworkMapper
): RecipeRepository {
    private  val TAG = RecipeRepositoryImpl::class.java.simpleName

    private val recipeDao = db.recipeDao()

    override fun getRecipes(queryString: String) = networkBoundResource(
        query = {
            val cacheRecipe = recipeDao.getRecipes(queryString)
            cacheRecipe.map { cacheMapper.fromEntityList(it) }

        },
        fetch = {
            apiService.get(query = queryString)
        },
        saveFetchResult = {
           val cacheEntity =  mapToCacheEntity(it.body(), queryString)

            db.withTransaction {
                recipeDao.deleteRecipes(queryString)
                cacheEntity.map { cacheRecipes -> recipeDao.insertRecipe(cacheRecipes) }
            }
        },
        shouldFetch = { recipe ->
            recipe.isNullOrEmpty()
        }


    )

    override suspend fun getSearchRecipes(queryString: String): Flow<List<Recipe>> {
        val query = db.recipeDao().searchRecipe(queryString)

          return  query.map {cacheMapper.fromEntityList(it)}


    }

    override suspend fun updateRecipes(recipe: Recipe) {
        db.recipeDao().updateRecipes(cacheMapper.mapToEntity(recipe))
    }

    override suspend fun getRecentlySearchedRecipe(isRecentRecipe: Boolean): Flow<List<Recipe>> {
        val recentRecipes = db.recipeDao().getRecentSearchedRecipes(true)
        return recentRecipes.map { cacheMapper.fromEntityList(it) }
    }

    override suspend fun getFavoriteRecipes(isFavorite: Boolean): Flow<List<Recipe>?> {
        val favoriteRecipes = db.recipeDao().getFavoriteRecipes(isFavorite)
        return  favoriteRecipes.map { cacheMapper.fromEntityList(it) }
    }

    private fun mapToCacheEntity(body: RecipeNetworkResponse?, type: String): List<RecipeCacheEntity> {
        val networkRecipes = mutableListOf<RecipeNetworkEntity>()
        body?.hits?.forEach {
            networkRecipes.add(it.recipe)
        }
        val recipe = networkMapper.fromEntityList(networkRecipes.toList())
        val domainRecipe =  recipe.map { it.copy(type=type) }
        Log.d(TAG, "mapToCacheEntity: $domainRecipe")
        return cacheMapper.toEntityList(domainRecipe)
    }


}

//MainCourse
//Breakfast
//salad
//Omelette
//Bread
//Dessert
//Prep
//Alcohol
//Drinks
//Side Dish
//Sauce
//Snacks
//Cereals

