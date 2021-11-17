package com.example.myapplication.presentation.ui.recipesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RecentSearchListItemBinding
import com.example.myapplication.databinding.TextHeaderItemBinding
import com.example.myapplication.domain.model.Recipe
import kotlinx.coroutines.*
import java.lang.ClassCastException

const val ITEM_VIEW_TYPE_HEADER = 1
const val ITEM_VIEW_TYPE_ITEM = 0

class RecentSearchAdapter(private val clickListener: OnRecentRecipeClickListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(RecentSearchDiffUtil) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                TextHeaderViewHolder.from(parent)
            }
            ITEM_VIEW_TYPE_ITEM -> {
                RecentSearchViewHolder.from(parent)
            }
            else -> throw ClassCastException("Unknown ViewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SearchItem -> ITEM_VIEW_TYPE_ITEM
            else -> super.getItemViewType(position)
        }
    }

    fun addHeaderAndSubmitList(list: List<Recipe>){
            adapterScope.launch {
                val items = if(!list.isNullOrEmpty()){
                    listOf(DataItem.Header) + list.map { DataItem.SearchItem(it) }
                }else{
                    return@launch
                }

                withContext(Dispatchers.Main){
                    submitList(items)
                }
            }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextHeaderViewHolder -> {
                holder.bind(clickListener)
            }
            is RecentSearchViewHolder -> {
                val item = getItem(position) as DataItem.SearchItem
                holder.bind(item.recipe, clickListener)
            }
        }
    }

    class TextHeaderViewHolder(private val binding: TextHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: OnRecentRecipeClickListener) {
                binding.clearAllText.setOnClickListener {
                    clickListener.onClearAllRecentClick()
                }
            binding.invalidateAll()
        }

        companion object {
            fun from(parent: ViewGroup): TextHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bind = TextHeaderItemBinding.inflate(layoutInflater, parent, false)
                return TextHeaderViewHolder(bind)
            }
        }
    }

    class RecentSearchViewHolder(private val binding: RecentSearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recipe, clickListener: OnRecentRecipeClickListener) {
            binding.recipe = item
            binding.invalidateAll()
            binding.deleteLayout.setOnClickListener {
                clickListener.onRemoveRecentRecipeClick(item)
            }
            binding.mainContainer.setOnClickListener {
                clickListener.onViewRecentRecipeClick(item)
            }



        }


        companion object {
            fun from(parent: ViewGroup): RecentSearchViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bind = RecentSearchListItemBinding.inflate(layoutInflater, parent, false)
                return RecentSearchViewHolder(bind)
            }
        }

    }

    object RecentSearchDiffUtil : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

    }

}

sealed class DataItem {
    abstract val id: Int

    data class SearchItem(val recipe: Recipe) : DataItem() {
        override val id: Int
            get() = recipe.id

    }

    object Header : DataItem() {
        override val id: Int
            get() = Long.MIN_VALUE.toInt()

    }
}

interface OnRecentRecipeClickListener{
    fun onRemoveRecentRecipeClick(recipe: Recipe)
    fun onViewRecentRecipeClick(recipe: Recipe)
    fun onClearAllRecentClick()
}
