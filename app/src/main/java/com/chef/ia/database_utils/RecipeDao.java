package com.chef.ia.database_utils;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.chef.ia.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes")
    List<Recipe> loadAllRecipes();

    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe);

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe getRecipe(int id);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("DELETE FROM recipes")
    void deleteAllRecipes();
}
