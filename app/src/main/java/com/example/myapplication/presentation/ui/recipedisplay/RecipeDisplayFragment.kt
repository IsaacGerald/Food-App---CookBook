package com.example.myapplication.presentation.ui.recipedisplay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRecipeDisplayBinding
import com.example.myapplication.domain.model.Recipe
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecipeDisplayFragment : Fragment(R.layout.fragment_recipe_display) {
    private lateinit var binding: FragmentRecipeDisplayBinding
    private val viewModel: RecipeDisplayViewModel by viewModels()
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipeDisplayBinding.bind(view)

        val recipe: Recipe = RecipeDisplayFragmentArgs.fromBundle(requireArguments()).recipe
        viewModel.recipeState.value = recipe

        bindToolBar()
        setHasOptionsMenu(true)

        viewModel.recipeState.observe(viewLifecycleOwner, Observer {
            it?.let { recipe ->
                initWebView(recipe)
            }
        })
    }

    private fun bindToolBar() {
        (activity as MainActivity).setSupportActionBar(binding.recipeDisplayToolbar)
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(
            binding.recipeDisplayToolbar,
            navController,
            appBarConfiguration
        )
        binding.recipeDisplayToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(recipe: Recipe) {
        binding.recipeDisplayToolbar.title = recipe.label
        val web = binding.webView
        val sprite: Sprite = Circle()
        progressBar = binding.spinKit
        progressBar.indeterminateDrawable = sprite

        web.apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            this.webChromeClient = WebChromeClient()
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }

        }
        if (recipe.url != null) {
            web.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                }

            }

            web.loadUrl(recipe.url)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_display_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                showToast("Item Clicked!")
                true
            }
            R.id.action_share -> {
                shareRecipe()
            }
            else ->
                NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                ) || super.onOptionsItemSelected(item)
        }


    }

    private fun shareRecipe(): Boolean {
        var isShared = false

        viewModel.recipeState.observe(viewLifecycleOwner, Observer {
            it?.let { recipe ->
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this ${recipe.label} recipe: ${recipe.shareAs}"
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, recipe.label)
                startActivity(shareIntent)
                isShared = true
            }
        })

      return isShared
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}


