package com.chef.ia.database_utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.chef.ia.models.Recipe;


@Database(entities = {Recipe.class}, version = 1, exportSchema = false)

public abstract class RecipeDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "RecipesDB";
    private static RecipeDatabase sInstance;
    private static final Object LOCK = new Object();

    public abstract RecipeDao recipeDao();

    public static RecipeDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), RecipeDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
            }
        }
        return sInstance;
    }
}
