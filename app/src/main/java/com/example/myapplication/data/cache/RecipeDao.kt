package com.example.myapplication.data.cache

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_table WHERE type LIKE '%' || :query || '%'")
    fun getRecipes(query: String): Flow<List<RecipeCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipeCacheEntity: RecipeCacheEntity)

    @Query("DELETE FROM recipe_table WHERE type= :type")
    suspend fun deleteRecipes(type: String)

    @Query("SELECT * FROM recipe_table WHERE label LIKE '%' || :query || '%'")
    fun searchRecipe(query: String): Flow<List<RecipeCacheEntity>>

    @Update
    suspend fun updateRecipes(recipeCacheEntity: RecipeCacheEntity)

    @Query("SELECT * FROM recipe_table WHERE isRecentlyViewed= :isRecentRecipe")
    fun getRecentSearchedRecipes(isRecentRecipe: Boolean): Flow<List<RecipeCacheEntity>>

    @Query("SELECT * FROM recipe_table WHERE isFavorite= :isFavorite")
    fun getFavoriteRecipes(isFavorite: Boolean): Flow<List<RecipeCacheEntity>>

//    @Query("DELETE FROM recipe_table WHERE id= :id")
//    suspend fun deleteFavoriteRecipes(id: Int)
}