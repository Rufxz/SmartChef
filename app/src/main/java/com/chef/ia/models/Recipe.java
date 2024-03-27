package com.chef.ia.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "recipes")
public class Recipe implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String recipe;
    private long timestamp;
    private String ingredients;

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Recipe(int id, String name, String recipe, long timestamp, String ingredients) {
        this.id = id;
        this.name = name;
        this.recipe = recipe;
        this.timestamp = timestamp;
        this.ingredients = ingredients;
    }

    @Ignore
    public Recipe(String name, String recipe, long timestamp, String ingredients) {
        this.name = name;
        this.recipe = recipe;
        this.timestamp = timestamp;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
