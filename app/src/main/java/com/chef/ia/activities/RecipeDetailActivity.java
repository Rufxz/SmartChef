package com.chef.ia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.chef.ia.utils.Constants;
import com.chef.ia.database_utils.RecipeDatabase;
import com.chef.ia.databinding.ActivityRecipeDetailBinding;
import com.chef.ia.models.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {
    ActivityRecipeDetailBinding binding;
    RecipeDatabase db;
    Recipe recipe;
    boolean isNewRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        swapToolbar();

        db = RecipeDatabase.getInstance(this);

        recipe = (Recipe) getIntent().getSerializableExtra(Constants.RECIPE);
        populateViews(recipe);


        isNewRecipe = getIntent().getBooleanExtra(Constants.IS_NEW_RECIPE, false);
        if (isNewRecipe) binding.appBar.saveRecipeBtn.setVisibility(View.VISIBLE);

    }

    private void populateViews(Recipe recipe) {
        binding.dishNameTv.setText(recipe.getName());
        binding.recipeTv.setText(recipe.getRecipe());
        binding.ingredientsTv.setText(recipe.getIngredients());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void swapToolbar() {
        setSupportActionBar(binding.appBar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.appBar.saveRecipeBtn.setOnClickListener(view -> {
            db.recipeDao().insertRecipe(recipe);
            binding.appBar.saveRecipeBtn.setText("Saved");
            binding.appBar.saveRecipeBtn.setClickable(false);
        });
    }
}