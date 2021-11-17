package com.example.myapplication.di

import com.example.myapplication.data.cache.RecipeCacheMapper
import com.example.myapplication.data.cache.RecipeDatabase
import com.example.myapplication.data.network.ApiService
import com.example.myapplication.data.network.model.RecipeNetworkMapper
import com.example.myapplication.domain.RecipeRepository
import com.example.myapplication.domain.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesRecipeRepository(
         database: RecipeDatabase,
         apiService: ApiService,
         networkMapper: RecipeNetworkMapper,
         cacheMapper: RecipeCacheMapper

    ): RecipeRepository{
        return RecipeRepositoryImpl(
            db = database,
            apiService = apiService,
            networkMapper = networkMapper,
            cacheMapper = cacheMapper
        )
    }
}