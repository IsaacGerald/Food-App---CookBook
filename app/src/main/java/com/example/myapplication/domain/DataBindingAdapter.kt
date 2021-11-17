package com.example.myapplication.domain

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.domain.model.Recipe

@SuppressLint("CheckResult")
@BindingAdapter("recipeImageView")
fun bindRecipeImage(imageView: ImageView, recipe: Recipe){

    val imageUrl = Uri.parse(recipe.image)

    Glide.with(imageView)
        .load(imageUrl)
        .apply(RequestOptions().apply {
            placeholder(R.drawable.meal_placeholder)
            error(R.drawable.meal_placeholder)
        }
            )
        .into(imageView)


}