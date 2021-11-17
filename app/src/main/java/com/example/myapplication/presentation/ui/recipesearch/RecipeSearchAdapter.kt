package com.example.myapplication.presentation.ui.recipesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.SearchListItemBinding
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.presentation.ui.recipesearch.RecipeSearchAdapter.SearchViewHolder.Companion.from

class RecipeSearchAdapter
    (private val clickListener: OnSearchRecipeClickListener) :
    ListAdapter<Recipe, RecipeSearchAdapter.SearchViewHolder>(SearchDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class SearchViewHolder(private val binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recipe, clickListener: OnSearchRecipeClickListener) {
            binding.recipe = item
            binding.invalidateAll()
            binding.recipeSearchContainer.setOnClickListener {
                clickListener.onSearchRecipeClick(item)
            }

        }


        companion object {
            fun from(parent: ViewGroup): SearchViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bind = SearchListItemBinding.inflate(layoutInflater, parent, false)
                return SearchViewHolder(bind)
            }
        }

    }

    object SearchDiffUtil : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }

    }

}

interface OnSearchRecipeClickListener {
    fun onSearchRecipeClick(recipe: Recipe)
}