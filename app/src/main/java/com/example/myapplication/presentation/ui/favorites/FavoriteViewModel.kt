package com.example.myapplication.presentation.ui.favorites

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.RecipeRepository
import com.example.myapplication.domain.RecipeRepositoryImpl
import com.example.myapplication.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
@Inject constructor(
    private val repository: RecipeRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val TAG = FavoriteViewModel::class.java.simpleName
    private val _favoriteRecipes = MutableStateFlow<List<Recipe>?>(null)
    private val _stateManager = MainStateManager()
    val favoriteRecipes: StateFlow<List<Recipe>?>
        get() = _favoriteRecipes
    val stateManager: MainStateManager
        get() = _stateManager

    fun getFavoriteRecipes() {
        viewModelScope.launch {
            repository.getFavoriteRecipes(true).collect { recipes ->
                _favoriteRecipes.value = recipes
            }
        }
    }

    fun clearSelectedNodes() {
        viewModelScope.launch {
            val list = stateManager.selectedNodes.value
            Log.d(TAG, "clearSelectedNodes: ${list?.map { it.label }}")
            list?.map {
                repository.updateRecipes(it.copy(isFavorite = false))
            }

        }
        _stateManager.clearSelectedList()
        getFavoriteRecipes()
        stateManager.setToolBarState(ToolBarState.NormalViewState)
    }

    fun clearAllNodes() {
        viewModelScope.launch {
             val favorites = _favoriteRecipes.value
            favorites?.forEach {
                repository.updateRecipes(it.copy(isFavorite = false))
            }

        }
        getFavoriteRecipes()
        stateManager.setToolBarState(ToolBarState.NormalViewState)

    }
}

