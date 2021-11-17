package com.example.myapplication.presentation.ui.recipesearch

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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject constructor(
    private val repository: RecipeRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val _queryResult = MutableStateFlow<DataState<List<Recipe>>?>(null)
    private val _suggestionList = MutableStateFlow<List<Recipe>?>(null)
//    private val _isSearchCompleted = MutableStateFlow<Boolean>(false)
    private val _recentlySearchedRecipes = MutableStateFlow<List<Recipe>?>(null)
    private val _recipesSearchTaskChannel = Channel<RecipeSearchTask>()
    val queryString = state.getLiveData<String>("queryString")
    val searchString = state.getLiveData<String>("searchString")
    val receivedQuery = state.getLiveData<String>("receivedQuery")
    val isSearchCompleted = state.getLiveData<Boolean>("IsSearchCompleted", false)

    val recipeSearchTask = _recipesSearchTaskChannel.receiveAsFlow()
    val queryResult: StateFlow<DataState<List<Recipe>>?>
        get() = _queryResult
    val suggestionList: StateFlow<List<Recipe>?>
        get() = _suggestionList
    val recentlySearchedRecipes: StateFlow<List<Recipe>?>
        get() = _recentlySearchedRecipes
//    val isSearchCompleted: StateFlow<Boolean>
//        get() = _isSearchCompleted


    fun getRecipes() {
        viewModelScope.launch {
            if (!searchString.value.isNullOrBlank()) {
                val result = repository.getRecipes(searchString.value!!)
                result.collect {
                    _recipesSearchTaskChannel.send(RecipeSearchTask.GetRecipes(it))
                }
            } else {
                getRecentlyViewedRecipes()
            }

        }
    }

    fun getSearchRecipe() {
        if (!searchString.value.isNullOrEmpty()) {
            viewModelScope.launch {
                repository.getSearchRecipes(searchString.value!!).collect {
                    _recipesSearchTaskChannel.send(RecipeSearchTask.ShowSuggestedRecipes(it))
                }

            }
        }

    }

    fun updateToRecentlySearchedRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipes(recipe.copy(isRecentlyViewed = true))
        }
    }

    fun getRecentlyViewedRecipes() {
        viewModelScope.launch {
            repository.getRecentlySearchedRecipe(true).collect {
                _recipesSearchTaskChannel.send(RecipeSearchTask.ShowRecentlyViewedRecipes(it))
            }
        }
    }

    fun removeRecentRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipes(recipe.copy(isRecentlyViewed = false))
        }
        getRecentlyViewedRecipes()
    }

    fun checkIsSearchCompleted() {
        viewModelScope.launch {
            _recipesSearchTaskChannel.send(RecipeSearchTask.IsSearchCompleted(isSearchCompleted.value!!))
        }
    }




}

sealed class RecipeSearchTask {
    data class ShowRecentlyViewedRecipes(val recipes: List<Recipe>?) : RecipeSearchTask()
    data class ShowSuggestedRecipes(val recipe: List<Recipe>?) : RecipeSearchTask()
    data class GetRecipes(val state: DataState<List<Recipe>>) : RecipeSearchTask()
    data class IsSearchCompleted(val isCompleted: Boolean) : RecipeSearchTask()
}