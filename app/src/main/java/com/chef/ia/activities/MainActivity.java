package com.chef.ia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chef.ia.adapters.RecipesAdapter;
import com.chef.ia.adapters.RecipesAdapter.OnRecipeDeletedListener;
import com.chef.ia.database_utils.RecipeDatabase;
import com.chef.ia.databinding.ActivityMainBinding;
import com.chef.ia.models.Recipe;
import com.chef.ia.utils.AdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnRecipeDeletedListener {
    ActivityMainBinding binding;
    RecipesAdapter adapter;
    private RecipeDatabase db;
    private List<Recipe> allRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = RecipeDatabase.getInstance(this);

        setListeners();
        AdUtils.initializeAdsAndShowBannerAd(this, binding.adView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllRecipesFromDB();
    }

    private void setListeners() {
        binding.createRecipeTv.setOnClickListener(view -> startActivity(new Intent(this, CreateRecipeActivity.class)));
    }

    public static List<String> getDummyData() {
        List<String> dummy = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dummy.add("");
        }
        return dummy;
    }

    private void loadAllRecipesFromDB() {
        allRecipes = db.recipeDao().loadAllRecipes();
        if (allRecipes.size() > 0) {
            binding.noRecipeAnimation.setVisibility(View.GONE);
        } else {
            binding.noRecipeAnimation.setVisibility(View.VISIBLE);
        }
        adapter = new RecipesAdapter(allRecipes, this);
        binding.totalRecipesTv.setText(String.format(Locale.getDefault(), "Total: %s", allRecipes.size()));
        binding.recipesRv.setLayoutManager(new LinearLayoutManager(this));
        binding.recipesRv.setAdapter(adapter);
    }

    @Override
    public void onRecipeDeleted(int count) {
        if (count == 0) {
            binding.noRecipeAnimation.setVisibility(View.VISIBLE);
        }
        binding.totalRecipesTv.setText(String.format(Locale.getDefault(), "Total: %s", count));
    }
}
