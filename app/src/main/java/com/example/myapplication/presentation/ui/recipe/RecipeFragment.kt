package com.example.myapplication.presentation.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRecipeBinding
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.DataState
import com.example.myapplication.presentation.ui.recipedisplay.RecipeDisplayFragmentArgs
import com.google.android.material.chip.Chip
import com.miguelcatalan.materialsearchview.MaterialSearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class RecipeFragment : Fragment(R.layout.fragment_recipe), RecipeOnClickListener {
    private lateinit var binding: FragmentRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter
    private val TAG = RecipeFragment::class.java.simpleName
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipeBinding.bind(view)


        bindToolBar()
        bindChipGroup()
        initSearchView()
        setHasOptionsMenu(true)

        recipeAdapter = RecipeAdapter(this)
        binding.recyclerViewDish.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = recipeAdapter

            hasFixedSize()

        }

        viewModel.selectedChip.observe(viewLifecycleOwner, Observer { selectedChip ->
            viewModel.searchQuery.value = selectedChip
            viewModel.getRecipes()
        })




        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipes.collect { state ->
                when (state) {
                    is DataState.Success -> {
                        Log.d(TAG, "onViewCreated: fetch successful -> ${state.data} ")
                        recipeAdapter.submitList(state.data)
                        hideProgressBar()
                    }
                    is DataState.Loading -> {
                        if (!state.data.isNullOrEmpty()) {
                            recipeAdapter.submitList(state.data)
                            hideProgressBar()
                        } else {
                            showProgressBar()
                        }
                    }
                    is DataState.Error -> {
                        Log.d(TAG, "onViewCreated: error when fetching -> ${state.error} ")
                    }

                }

            }
        }

//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.queryResult.collect { recipes ->
//                if (!recipes.isNullOrEmpty()) {
//                    binding.materialSearch.setSuggestions(getSuggestionList(recipes))
//                }
//            }
//        }

    }

//    private fun getSuggestionList(suggestionList: List<Recipe>) =
//        suggestionList.map { it.label }.toTypedArray()

    private fun initSearchView() {
        binding.materialSearch.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    findNavController().navigate(
                        RecipeFragmentDirections.actionRecipeFragmentToRecipeSearchFragment(
                            query
                        )
                    )
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                if (!newText.isNullOrBlank()) {
//                    viewModel.getSearchRecipe(newText)
//                     return true
//                }

                return false
            }

        })
    }

    private fun bindChipGroup() {
        val view = binding.chipsIncludeLayout
        val chipGroup = view.chipGroup
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId)
            if (chip != null) {
                val query = chip.text.toString()
                viewModel.selectedChip.value = query

            }

        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewDish.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewDish.visibility = View.VISIBLE
    }

    private fun bindToolBar() {
        (activity as MainActivity).setSupportActionBar(binding.homeToolbar)
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(binding.homeToolbar, navController, appBarConfiguration)
        binding.homeToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val item = menu.findItem(R.id.action_search)
        binding.materialSearch.setMenuItem(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    override fun recipeOnClick(recipe: Recipe) {

        findNavController().navigate(
            RecipeFragmentDirections.actionRecipeFragmentToRecipeDisplayFragment(
                recipe
            )
        )

    }

    override fun onFavClick(recipe: Recipe) {
        viewModel.onFavClick(recipe)
    }
}