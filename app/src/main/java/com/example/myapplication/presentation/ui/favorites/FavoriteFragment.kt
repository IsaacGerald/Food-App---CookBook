package com.example.myapplication.presentation.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFavoriteBinding
import com.example.myapplication.domain.model.Recipe
import com.miguelcatalan.materialsearchview.MaterialSearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favAdapter: FavoriteRecipeAdapter
    private val viewModel: FavoriteViewModel by viewModels()
    private val TAG = FavoriteFragment::class.java.simpleName
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)

        bindToolBar()
        bindUI()
        setHasOptionsMenu(true)
        initSearchView()
        setRecyclerView()

        binding.favoriteRecyclerView.apply {
            //layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = favAdapter
        }

        viewModel.getFavoriteRecipes()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.favoriteRecipes.collect { recipes ->
                if (!recipes.isNullOrEmpty()) {
                    favAdapter.submitList(recipes)
                }
            }
        }

        viewModel.stateManager.selectedNodes.observe(viewLifecycleOwner, Observer { recipes ->
            recipes?.map {
                Log.d(TAG, "onViewCreated: SelectedNodes -> ${it.label} ")
            }
                viewModel.stateManager.toolBarState.observe(viewLifecycleOwner, Observer { state ->
                    when(state){
                        ToolBarState.MultiSelectionState -> {
                            binding.favoriteToolbar.subtitle = "${recipes?.size} Selected"
                        }
                        ToolBarState.NormalViewState -> {
                            binding.favoriteToolbar.subtitle = null
                        }
                        else -> return@Observer
                    }
                })


                Log.d(TAG, "onViewCreated: ------------------------------------------------")
        })

    }

    private fun setRecyclerView() {
        favAdapter = FavoriteRecipeAdapter(
            onLongClick = { recipe ->
                viewModel.stateManager.apply {
                    setToolBarState(ToolBarState.MultiSelectionState)
                    addOrRemoveRecipesFromSelectedList(recipe)
                }
            },
            onClick = { recipe ->
                if (viewModel.stateManager.isMultiSelectionActive()) {
                    viewModel.stateManager.addOrRemoveRecipesFromSelectedList(recipe)
                } else {
                    navigateToDisplayRecipe(recipe)
                }

            },
            selectedRecipes = viewModel.stateManager.selectedNodes,
            context = requireContext(),
            parentLifecycleOwner = viewLifecycleOwner


        )
    }

    private fun navigateToDisplayRecipe(recipe: Recipe) {
        findNavController().navigate(
            FavoriteFragmentDirections.actionFavoriteFragmentToRecipeDisplayFragment(
                recipe
            )
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun bindUI() {
        viewModel.stateManager.toolBarState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                ToolBarState.NormalViewState -> {
                    setNormalToolBar()
                    viewModel.stateManager.clearSelectedList()
                }
                ToolBarState.MultiSelectionState -> {
                    setSelectedToolBar()
                }
                else -> {
                    // TODO: When exhaustive
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSelectedToolBar() {
        binding.favoriteToolbar.apply {
            menu.clear()
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorGrey))
            inflateMenu(R.menu.selected_menu)
            navigationIcon = requireContext().getDrawable(R.drawable.ic_close_24)
            setNavigationOnClickListener {
                viewModel.stateManager.setToolBarState(ToolBarState.NormalViewState)
            }
        }

    }

    private fun setNormalToolBar() {
        binding.favoriteToolbar.apply {
            navigationIcon = null
            setNavigationOnClickListener(null)
            menu.clear()
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            inflateMenu(R.menu.favorite_menu)
            binding.materialSearch.setMenuItem(menu.findItem(R.id.action_search))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        val item = menu.findItem(R.id.action_search)
        binding.materialSearch.setMenuItem(item)
    }


    private fun bindToolBar() {
        (activity as MainActivity).setSupportActionBar(binding.favoriteToolbar)
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(
            binding.favoriteToolbar,
            navController,
            appBarConfiguration
        )
        binding.favoriteToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete -> {
                clearSelectedRecipes()
            }
            R.id.action_clearAll -> {
                clearAllRecipes()
            }
            R.id.action_selectAll -> {
                selectAllRecipes()
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
        return true

    }

    private fun selectAllRecipes() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.favoriteRecipes.collect { recipes ->
                if (!recipes.isNullOrEmpty()) {
                    viewModel.stateManager.addAllToSelectedList(recipes)
                }
            }
        }

    }

    private fun clearAllRecipes() {
        viewModel.clearAllNodes()
    }

    private fun clearSelectedRecipes() {
        viewModel.clearSelectedNodes()
    }

    private fun initSearchView() {
        binding.materialSearch.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    findNavController().navigate(
                        FavoriteFragmentDirections.actionFavoriteFragmentToRecipeSearchFragment(
                            query
                        )
                    )

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO: QueryTextChange
                return false
            }

        })
    }
}