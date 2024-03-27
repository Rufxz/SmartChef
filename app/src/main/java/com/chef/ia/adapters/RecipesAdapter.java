package com.chef.ia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.recyclerview.widget.RecyclerView;

import com.chef.ia.utils.Constants;
import com.chef.ia.activities.RecipeDetailActivity;
import com.chef.ia.database_utils.RecipeDatabase;
import com.chef.ia.databinding.MainRvItemBinding;
import com.chef.ia.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    Context context;
    List<Recipe> recipes = new ArrayList<>();
    OnRecipeDeletedListener listener;
    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        MainRvItemBinding binding = MainRvItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    public RecipesAdapter(List<Recipe> recipes, OnRecipeDeletedListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.binding.dishNameTv.setText(recipe.getName());
        if (recipe.getIngredients().length() > 25) {
            holder.binding.ingredientsTv.setText(recipe.getIngredients().substring(0, 25) + " & more...");
        } else {
            holder.binding.ingredientsTv.setText(recipe.getIngredients());
        }
        holder.binding.dayTv.setText(convertPublishTime(recipe.getTimestamp()));
        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, RecipeDetailActivity.class).putExtra(Constants.RECIPE, recipe)));
        holder.binding.deleteRecipeBtn.setOnClickListener(view -> {
            new Builder(context)
                    .setMessage("Delete this recipe?")
                    .setCancelable(false)
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Yes", (dialogInterface, i) -> deleteRecipe(recipe)).show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private void deleteRecipe(Recipe recipe) {
        RecipeDatabase db = RecipeDatabase.getInstance(context);
        db.recipeDao().deleteRecipe(recipe);
        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        recipes.remove(recipe);
        listener.onRecipeDeleted(getItemCount());
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MainRvItemBinding binding;

        public ViewHolder(@NonNull MainRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static String convertPublishTime(long pastTimestamp) {
        pastTimestamp *= 1000;
        long currentTimestamp = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTimestamp - pastTimestamp);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - pastTimestamp);
        long hours = TimeUnit.MILLISECONDS.toHours(currentTimestamp - pastTimestamp);
        long days = TimeUnit.MILLISECONDS.toDays(currentTimestamp - pastTimestamp);
        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hrs ago";
        } else {
            return days + " days ago";
        }
    }

    public interface OnRecipeDeletedListener{
        void onRecipeDeleted(int count);
    }
}