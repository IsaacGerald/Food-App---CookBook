package com.example.myapplication.presentation.ui.recipe

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.RecipeRepository
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecipeViewModel
@Inject constructor(
    private val repository: RecipeRepository,
    private val state: SavedStateHandle
): ViewModel() {
    private val TAG = RecipeViewModel::class.java.simpleName
    private val recipeEventChannel = Channel<RecipeStateEvent>()
    val searchQuery = state.getLiveData("searchQuery", "")
    val selectedChip = state.getLiveData("selectedChip", "Main course")
    private val _recipes = MutableStateFlow<DataState<List<Recipe>>?>(null)
    val recipes: StateFlow<DataState<List<Recipe>>?>
    get() = _recipes
    private val _queryResult = MutableStateFlow<List<Recipe>?>(null)
    val queryResult: StateFlow<List<Recipe>?>
        get() = _queryResult




    fun getRecipes() {
        viewModelScope.launch {
            if (searchQuery.value != null){
               val result = repository.getRecipes(searchQuery.value!!)
                result.collect {
                    _recipes.value = it
                }
            }

        }
    }


    fun getSearchRecipe(query: String) {
        viewModelScope.launch {
            repository.getSearchRecipes(query).collect {
                _queryResult.value = it
            }

        }

    }

    fun onFavClick(recipe: Recipe) {
        viewModelScope.launch {
            if (recipe.isFavorite){
                repository.updateRecipes(recipe.copy(isFavorite = false))
            }else{
                repository.updateRecipes(recipe.copy(isFavorite = true))
            }
        }
       getRecipes()
    }

}

