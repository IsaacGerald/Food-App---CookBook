package com.example.myapplication.presentation.ui.favorites

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.RecipeListItemBinding
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.presentation.ui.favorites.FavoriteRecipeAdapter.FavoriteRecipeViewHolder.Companion.from

class FavoriteRecipeAdapter(
    private val context: Context,
    private val parentLifecycleOwner: LifecycleOwner,
    private val selectedRecipes: MutableLiveData<List<Recipe>?>,
    private val onClick: (Recipe) -> Unit,
    private val onLongClick: (Recipe) -> Unit

) : ListAdapter<Recipe, FavoriteRecipeAdapter.FavoriteRecipeViewHolder>(FavRecipeDiffCallback) {
    private  val TAG = FavoriteRecipeAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        return from(parent, parentLifecycleOwner)
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(context, item, onClick, onLongClick, selectedRecipes)
    }


    class FavoriteRecipeViewHolder(
        private val binding: RecipeListItemBinding,
        private val lifecycleOwner: LifecycleOwner
    ) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        private val TAG = FavoriteRecipeAdapter::class.java.simpleName
        val viewLifeCycleRegistry = RecyclerViewLifeCycleRegistry(this, lifecycleOwner.lifecycle)
        private val lifecycleRegistry = LifecycleRegistry(this)

        fun bind(
            context: Context,
            item: Recipe,
            onClick: (Recipe) -> Unit,
            onLongClick: (Recipe) -> Unit,
            selectedRecipes: MutableLiveData<List<Recipe>?>,

            ) {
            binding.recipe = item

            selectedRecipes.observe(lifecycleOwner, Observer { recipes ->
                if (recipes!!.contains(item)) {
                    binding.root.setBackgroundColor(context.getColor(R.color.grey))
                } else {
                    binding.root.setBackgroundColor(Color.TRANSPARENT)
                }
            })

            binding.root.setOnClickListener {
                onClick.invoke(item)
            }

            binding.root.setOnLongClickListener {
                onLongClick.invoke(item)
                true
            }

            binding.invalidateAll()
        }


        companion object {
            fun from(
                parent: ViewGroup,
                parentLifecycleOwner: LifecycleOwner
            ): FavoriteRecipeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bind = RecipeListItemBinding.inflate(layoutInflater, parent, false)
                val holder = FavoriteRecipeViewHolder(bind, parentLifecycleOwner)
                bind.lifecycleOwner = holder

                return holder
            }
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun setCreated(){
            Log.d(TAG, "setCreated: ViewHolder Created    {\" + this + \"} ")

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }



    }

    override fun onViewAttachedToWindow(holder: FavoriteRecipeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.viewLifeCycleRegistry.highestState = Lifecycle.State.RESUMED
        Log.d(TAG, "onViewAttachedToWindow called.. ")
    }

    override fun onViewDetachedFromWindow(holder: FavoriteRecipeViewHolder) {
        holder.viewLifeCycleRegistry.highestState = Lifecycle.State.CREATED
        Log.d(TAG, "onViewDetachedFromWindow called..")
        super.onViewDetachedFromWindow(holder)
    }

    object FavRecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }

    }

}

interface RecipeOnClickListener {

    fun recipeOnClick(recipe: Recipe)
}

class RecyclerViewLifeCycleRegistry(owner: LifecycleOwner, private val parent: Lifecycle) :
    LifecycleRegistry(owner) {
    private val TAG = FavoriteRecipeAdapter::class.java.simpleName
        private val parentLifeCycleObserver = object: LifecycleObserver{
            @OnLifecycleEvent(Event.ON_ANY)
            fun onAny(){
                currentState = parent.currentState
                Log.d(TAG, "onAny: ${parent.currentState} ")

            }
        }
    var highestState = State.INITIALIZED
    set(value) {
        field = value
        if (parent.currentState == highestState){
            currentState = value
        }
    }

    init {
        observeParent()
    }

    private fun observeParent(){
        parent.addObserver(parentLifeCycleObserver)
    }

    private fun ignoreParent(){
        parent.removeObserver(parentLifeCycleObserver)
    }

    override fun setCurrentState(state: State) {
        val maxNextState = if(state > highestState)
            highestState else state
        if (state == State.DESTROYED){
            ignoreParent()
        }
        super.setCurrentState(maxNextState)
    }
}