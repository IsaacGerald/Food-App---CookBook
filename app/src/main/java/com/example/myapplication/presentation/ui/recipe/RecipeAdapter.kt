package com.example.myapplication.presentation.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.RecipeListItemBinding
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.presentation.ui.recipe.RecipeAdapter.RecipeViewHolder.Companion.from

class RecipeAdapter(
   private val recipeOnClickListener: RecipeOnClickListener
): ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, recipeOnClickListener)
    }



    class RecipeViewHolder(private val binding: RecipeListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(
            item: Recipe,
            recipeOnClickListener: RecipeOnClickListener
        ){
            binding.recipe = item
            binding.mealsImageView.clipToOutline = true
            var isFavorite = item.isFavorite
            if (item.isFavorite){
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_filled)
            }else{
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite)
            }

            binding.dishConstraint.setOnClickListener {
                recipeOnClickListener.recipeOnClick(item)
            }

            binding.favoriteBtn.setOnClickListener {
                recipeOnClickListener.onFavClick(item)
            }
            binding.invalidateAll()

//            binding.mealsImageView.clipToOutline = true
//            var isFavorite = item.isFavorite
//
//            binding.dishConstraint.setOnClickListener {
//                recipeOnClickListener.recipeOnClick(item)
//                isFavorite = !isFavorite
//            }
//
//            if (isFavorite){
//                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_filled)
//            }else{
//                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite)
//            }

        }


        companion object{
            fun from(parent: ViewGroup): RecipeViewHolder{
                 val layoutInflater = LayoutInflater.from(parent.context)
                 val bind = RecipeListItemBinding.inflate(layoutInflater, parent, false)
                 return RecipeViewHolder(bind)
            }
        }

    }



    object RecipeDiffCallback: DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }

    }

}

interface RecipeOnClickListener{

    fun recipeOnClick(recipe: Recipe)

    fun onFavClick(recipe: Recipe)
}