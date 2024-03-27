package com.chef.ia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chef.ia.databinding.IngredientsRvItemBinding;

import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    Context context;
    List<String> ingredients = new ArrayList<>();


    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        IngredientsRvItemBinding binding = IngredientsRvItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position) {
        holder.binding.ingredientNameTv.setText(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        IngredientsRvItemBinding binding;

        public ViewHolder(@NonNull IngredientsRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
        notifyDataSetChanged();
    }

    public void clearAllIngredients(){
        ingredients.removeAll(ingredients);
        notifyDataSetChanged();
    }
}