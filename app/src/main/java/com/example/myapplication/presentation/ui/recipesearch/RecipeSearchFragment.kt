package com.example.myapplication.presentation.ui.recipesearch

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRecipeSearchBinding

import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.DataState
import com.miguelcatalan.materialsearchview.MaterialSearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RecipeSearchFragment : Fragment(R.layout.fragment_recipe_search),
    OnSearchRecipeClickListener, OnRecentRecipeClickListener {
    private lateinit var binding: FragmentRecipeSearchBinding
    private lateinit var searchAdapter: RecipeSearchAdapter
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private val viewModel: SearchViewModel by viewModels()
    private val TAG = RecipeSearchFragment::class.java.simpleName
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipeSearchBinding.bind(view)
        searchAdapter = RecipeSearchAdapter(this)
        recentSearchAdapter = RecentSearchAdapter(this)

        bindToolBar()
        handleSearchViewStatus()
        initSearchView()
        setHasOptionsMenu(true)

        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        val query = RecipeSearchFragmentArgs.fromBundle(requireArguments()).queryString


        if (!query.isNullOrEmpty()) {
            viewModel.checkIsSearchCompleted()
            viewModel.receivedQuery.value = query
        } else {
            showSearchView()
        }


        viewModel.queryString.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrBlank()) {
                Log.d(TAG, "onViewCreated: queryString ->  $it")
                setToolBarTitle(it)
            }
        })


        viewModel.getRecipes()


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipeSearchTask.collect { task ->
                when (task) {
                    is RecipeSearchTask.ShowRecentlyViewedRecipes -> {
                        getRecentlyViewedRecipes(task.recipes)
                    }
                    is RecipeSearchTask.ShowSuggestedRecipes -> {
                        showSuggestedRecipes(task.recipe)
                    }
                    is RecipeSearchTask.GetRecipes -> {
                        observeStates(task.state)
                    }
                    is RecipeSearchTask.IsSearchCompleted -> {
                        handleReceivedQuerySearch(task.isCompleted)
                    }

                }
            }
        }


    }

    private fun handleReceivedQuerySearch(isCompleted: Boolean) {
        if (!isCompleted) {
            viewModel.receivedQuery.observe(viewLifecycleOwner, Observer {
                viewModel.searchString.value = it
                viewModel.getRecipes()
            })

        }
    }


    private fun showRecyclerView() {
        binding.txtNoResults.visibility = View.GONE
        binding.txtNoRecentRecipes.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.VISIBLE
    }


    private fun showNoRecipesMessage() {

        viewModel.queryString.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                binding.txtNoRecentRecipes.visibility = View.GONE
                binding.txtNoResults.visibility = View.VISIBLE
                binding.txtNoResults.text = "No results found for '$it' "
            }

        })

    }

    private fun observeStates(state: DataState<List<Recipe>>) {
        when (state) {
            is DataState.Success -> {
                if (!state.data.isNullOrEmpty()) {
                    Log.d(
                        TAG,
                        "onViewCreated: fetch successful -> ${state.data?.map { it.label }} "
                    )

                    showRecipes(state.data)

                } else {
                    showNoRecipesMessage()
                    hideProgressBar()

                }

            }
            is DataState.Loading -> {
                if (!state.data.isNullOrEmpty()) {
                    searchAdapter.submitList(state.data)
                    hideProgressBar()
                } else {
                    Log.d(TAG, "onViewCreated: Loading data...")
                    showProgressBar()
                }
            }
            is DataState.Error -> {
                Log.d(TAG, "onViewCreated: error when fetching -> ${state.error} ")
                showToast(state.error.toString())
            }

        }
    }

    private fun getRecentlyViewedRecipes(recipes: List<Recipe>?) {
        binding.searchRecyclerView.apply {
            adapter = recentSearchAdapter
        }

        if (!recipes.isNullOrEmpty()) {
            recentSearchAdapter.addHeaderAndSubmitList(recipes)
        } else {
            showNoRecentListMessage()
        }


    }

    private fun showNoRecentListMessage() {
        binding.txtNoRecentRecipes.visibility = View.VISIBLE
        binding.txtNoResults.visibility = View.GONE
        binding.txtNoRecentRecipes.text = getString(R.string.no_recently_viewed_recipes)
    }

    private fun showSuggestedRecipes(recipe: List<Recipe>?) {
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
        }

        if (!recipe.isNullOrEmpty()) {
            showRecyclerView()
            searchAdapter.submitList(recipe)
        } else {
            viewModel.getRecentlyViewedRecipes()
        }
    }


    private fun showRecipes(recipes: List<Recipe>) {
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
        }

        hideProgressBar()
        searchAdapter.submitList(recipes)


    }

    private fun showSearchView() {
        val searchView = binding.materialSearch
        val isAttached = searchView.isAttachedToWindow
        if (isAttached) {
            if (!searchView.isSearchOpen) {
                searchView.showSearch()
            }
        }

    }


    private fun showToast(queryString: String) {
        Toast.makeText(requireContext(), queryString, Toast.LENGTH_SHORT).show()
    }

    private fun getSuggestionList(suggestionList: List<Recipe>) =
        suggestionList.map { it.label }.toTypedArray()

    private fun initSearchView() {
        binding.materialSearch.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchString.value = query
                    viewModel.getRecipes()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchString.value = newText
                if (!newText.isNullOrBlank() && newText.length > 2) {

                    viewModel.getSearchRecipe()
                    viewModel.isSearchCompleted.value = true
                } else {

                    viewModel.getRecentlyViewedRecipes()

                }

                return false
            }

        })
    }

    private fun handleSearchViewStatus() {
        binding.materialSearch.setOnSearchViewListener(object :
            MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                viewModel.searchString.observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrBlank()) {
                        binding.materialSearch.setQuery(it, false)
                    }
                })
                viewModel.getRecipes()

            }

            override fun onSearchViewClosed() {
                viewModel.getRecipes()
            }

        })
    }

    private fun setToolBarTitle(query: String) {
        binding.searchToolbar.title = query
    }


    private fun bindToolBar() {
        (activity as MainActivity).setSupportActionBar(binding.searchToolbar)
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(
            binding.searchToolbar,
            navController,
            appBarConfiguration
        )
        binding.searchToolbar.setupWithNavController(navController, appBarConfiguration)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.action_search)
        binding.materialSearch.setMenuItem(item)
    }

    private fun showProgressBar() {
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchProgressBar.visibility = View.VISIBLE

    }

    private fun hideProgressBar() {
        binding.searchProgressBar.visibility = View.GONE
        binding.txtNoResults.visibility = View.GONE
        binding.txtNoRecentRecipes.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.VISIBLE

    }

    override fun onSearchRecipeClick(recipe: Recipe) {
        viewModel.updateToRecentlySearchedRecipe(recipe)
        findNavController().navigate(
            RecipeSearchFragmentDirections.actionRecipeSearchFragmentToRecipeDisplayFragment(
                recipe
            )
        )
    }

    override fun onRemoveRecentRecipeClick(recipe: Recipe) {
        viewModel.removeRecentRecipe(recipe)
    }

    override fun onViewRecentRecipeClick(recipe: Recipe) {
        findNavController().navigate(
            RecipeSearchFragmentDirections.actionRecipeSearchFragmentToRecipeDisplayFragment(
                recipe
            )
        )
    }

    override fun onClearAllRecentClick() {
        showToast("Clear All")
    }
}