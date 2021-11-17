package com.example.myapplication.data.network.model

import com.example.myapplication.domain.model.Ingredient
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.EntityMapper
import javax.inject.Inject

class RecipeNetworkMapper
@Inject constructor() : EntityMapper<RecipeNetworkEntity, Recipe> {
    override fun mapFromEntity(entity: RecipeNetworkEntity): Recipe {
        return Recipe(
            calories = entity.calories,
            cautions = entity.cautions,
            cuisineType = entity.cuisineType,
            dietLabels = entity.dietLabels,
            dishType = entity.dishType,
            healthLabels = entity.healthLabels,
            image = entity.image,
            ingredientLines = entity.ingredientLines,
            ingredients = entity.ingredients?.asIngredient(),
            label = entity.label,
            mealType = entity.mealType,
            shareAs = entity.shareAs,
            source = entity.source,
            totalTime = entity.totalTime,
            totalWeight = entity.totalWeight,
            uri = entity.uri,
            url = entity.url,
            yield = entity.yield,
            id = 0

        )
    }


    override fun mapToEntity(domainModel: Recipe): RecipeNetworkEntity {
        return RecipeNetworkEntity(
            calories = domainModel.calories,
            cautions = domainModel.cautions,
            cuisineType = domainModel.cuisineType,
            dietLabels = domainModel.dietLabels,
            dishType = domainModel.dishType,
            healthLabels = domainModel.healthLabels,
            image = domainModel.image,
            ingredientLines = domainModel.ingredientLines,
            ingredients = domainModel.ingredients?.asIngredientNetworkEntity(),
            label = domainModel.label,
            mealType = domainModel.mealType,
            shareAs = domainModel.shareAs,
            source = domainModel.source,
            totalTime = domainModel.totalTime,
            totalWeight = domainModel.totalWeight,
            uri = domainModel.uri,
            url = domainModel.url,
            yield = domainModel.yield
        )
    }

    fun toEntityList(domainModel: List<Recipe>): List<RecipeNetworkEntity>{
        return domainModel.map { mapToEntity(it) }
    }

    fun fromEntityList(entity: List<RecipeNetworkEntity>): List<Recipe>{
        return entity.map{ mapFromEntity(it) }
    }

    private fun List<IngredientNetworkEntity>.asIngredient(): List<Ingredient>{
        return map {
            Ingredient(
                foodCategory = it.foodCategory,
                foodId = it.foodId,
                image = it.image,
                text = it.text,
                weight = it.weight
            )
        }
    }

    private fun List<Ingredient>.asIngredientNetworkEntity(): List<IngredientNetworkEntity>{
        return map{
            IngredientNetworkEntity(
                foodCategory = it.foodCategory,
                foodId = it.foodId,
                image = it.image,
                text = it.text,
                weight = it.weight
            )
        }
    }


}