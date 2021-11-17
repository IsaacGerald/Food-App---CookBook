package com.example.myapplication.presentation.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.domain.model.Recipe

class MainStateManager {

    private val TAG = MainStateManager::class.java.simpleName
    private val _toolBarState = MutableLiveData<ToolBarState>(ToolBarState.NormalViewState)
    private val _selectedNodes = MutableLiveData<List<Recipe>?>()
    private val _isMultiSelectionEnabled = MutableLiveData<Boolean>()

    val toolBarState: LiveData<ToolBarState>
    get() = _toolBarState

    val selectedNodes: MutableLiveData<List<Recipe>?>
    get() = _selectedNodes

    val multiSelectionState: LiveData<Boolean>
    get() = _isMultiSelectionEnabled

    fun isMultiSelectionActive(): Boolean =
        _toolBarState.value == ToolBarState.MultiSelectionState

    fun setToolBarState(state: ToolBarState){
        _toolBarState.postValue(state)
    }

    fun clearSelectedList(){
        _selectedNodes.value = listOf()
    }

    fun addOrRemoveRecipesFromSelectedList(recipe: Recipe){
       Log.d(TAG, "addOrRemoveRecipesFromSelectedList: Recipe -> ${recipe.label}")
        val list = mutableListOf<Recipe>()
        val nodeList = _selectedNodes.value
        if (nodeList != null) {
            list.addAll(nodeList)
        }
//        Log.d(TAG, "addOrRemoveRecipesFromSelectedList: selectedNodes: Before -> ${_selectedNodes.value}")

            if (list.contains(recipe)){
                list.remove(recipe)
            }else{
                list.add(recipe)
            }

        _selectedNodes.postValue(list.toList())
//        Log.d(TAG, "addOrRemoveRecipesFromSelectedList: selectedNodes: After -> ${_selectedNodes.value}")
    }

    fun addAllToSelectedList(recipes: List<Recipe>) {
        _selectedNodes.postValue(recipes)
    }

}