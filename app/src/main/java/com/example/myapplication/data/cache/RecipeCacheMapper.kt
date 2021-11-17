package com.example.myapplication.data.cache

import com.example.myapplication.domain.model.Ingredient
import com.example.myapplication.domain.model.Recipe
import com.example.myapplication.domain.util.EntityMapper
import javax.inject.Inject

class RecipeCacheMapper
@Inject constructor(): EntityMapper<RecipeCacheEntity, Recipe> {

    override fun mapFromEntity(entity: RecipeCacheEntity): Recipe {
        return Recipe(
            id = entity.id,
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
            isFavorite = entity.isFavorite,
            isRecentlyViewed = entity.isRecentlyViewed,
            type = entity.type
        )
    }

    override fun mapToEntity(domainModel: Recipe): RecipeCacheEntity {
        return RecipeCacheEntity(
            calories = domainModel.calories,
            cautions = domainModel.cautions,
            cuisineType = domainModel.cuisineType,
            dietLabels = domainModel.dietLabels,
            dishType = domainModel.dishType,
            healthLabels = domainModel.healthLabels,
            image = domainModel.image,
            ingredientLines = domainModel.ingredientLines,
            ingredients = domainModel.ingredients?.asIngredientCacheEntity(),
            label = domainModel.label,
            mealType = domainModel.mealType,
            shareAs = domainModel.shareAs,
            source = domainModel.source,
            totalTime = domainModel.totalTime,
            totalWeight = domainModel.totalWeight,
            uri = domainModel.uri,
            url = domainModel.url,
            isFavorite = domainModel.isFavorite,
            isRecentlyViewed = domainModel.isRecentlyViewed,
            yield = domainModel.yield,
            id = domainModel.id,
            type = domainModel.type

        )
    }

    fun toEntityList(domainModel: List<Recipe>): List<RecipeCacheEntity>{
        return domainModel.map { mapToEntity(it) }
    }

    fun fromEntityList(entity: List<RecipeCacheEntity>): List<Recipe>{
        return entity.map { mapFromEntity(it) }
    }

    private fun List<IngredientCacheEntity>.asIngredient(): List<Ingredient>{
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

    private fun List<Ingredient>.asIngredientCacheEntity(): List<IngredientCacheEntity>{
        return map {
            IngredientCacheEntity(
                foodCategory = it.foodCategory,
                foodId = it.foodId,
                image = it.image,
                text = it.text,
                weight = it.weight
            )
        }
    }


}