package com.example.myapplication.presentation.ui.recipedisplay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeDisplayViewModel
 @Inject constructor(
     private val state: SavedStateHandle
 )  : ViewModel() {

     val recipeState: MutableLiveData<Recipe?> = state.getLiveData("recipe", null)

}