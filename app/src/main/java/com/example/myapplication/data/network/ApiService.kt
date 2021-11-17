package com.example.myapplication.data.network


import com.example.myapplication.data.network.model.RecipeNetworkResponse
import com.example.myapplication.domain.util.APP_ID
import com.example.myapplication.domain.util.APP_KEY
import com.example.myapplication.domain.util.TYPE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/recipes/v2")
    suspend fun get(
        @Query("app_id") appId: String = APP_ID,
        @Query("app_key") appKey: String = APP_KEY,
        @Query("q") query: String,
        @Query("type") type: String = TYPE
    ): Response<RecipeNetworkResponse>

}