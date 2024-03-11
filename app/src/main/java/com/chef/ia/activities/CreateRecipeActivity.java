package com.chef.ia.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chef.ia.utils.AdUtils;
import com.chef.ia.utils.Constants;
import com.chef.ia.R;
import com.chef.ia.adapters.IngredientsAdapter;
import com.chef.ia.databinding.ActivityCreateRecipeBinding;
import com.chef.ia.databinding.SkillLevelPickerDialogBinding;
import com.chef.ia.models.Recipe;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateRecipeActivity extends AppCompatActivity {
    ActivityCreateRecipeBinding binding;
    IngredientsAdapter adapter;
    String ingredients = "";
    AlertDialog progressDialog;
    String skillLevel;
    ProgressDialog loadingAdProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        swapToolbar();

        setupIngredientsRv();
        setListeners();
    }

    private void setupIngredientsRv() {
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        binding.ingredientsRv.setLayoutManager(flexboxLayoutManager);
        adapter = new IngredientsAdapter();
        binding.ingredientsRv.setAdapter(adapter);
    }

    private void setListeners() {
        binding.createRecipeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ingredients.isEmpty()) {
                    Toast.makeText(CreateRecipeActivity.this, "No ingredients entered", Toast.LENGTH_SHORT).show();
                    return;
                }
                showSkillPickerDialog();
            }
        });

        binding.addIngredientBtn.setOnClickListener(view -> {

            String newIngredient = binding.ingredientInputEt.getText().toString().trim();

            if (newIngredient.isEmpty()) {
                binding.ingredientInputEt.setError("Please enter an ingredient");
                return;
            }

            String capitalizedIngredient = newIngredient.substring(0, 1).toUpperCase() + newIngredient.substring(1);
            adapter.addIngredient(capitalizedIngredient);
            ingredients = ingredients + capitalizedIngredient + ", ";
            binding.ingredientInputEt.setText("");
        });

        binding.clearAllIngredientsTv.setOnClickListener(view -> {
            adapter.clearAllIngredients();
            ingredients = "";
        });
    }

    private void showSkillPickerDialog() {
        SkillLevelPickerDialogBinding dialogBinding = SkillLevelPickerDialogBinding.inflate(getLayoutInflater());
        AlertDialog skillPickerDialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();
        skillPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        skillPickerDialog.show();
        dialogBinding.continueBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                skillLevel = "intermediate";
                if (dialogBinding.lowSkillRadioBtn.isChecked()) {
                    skillLevel = "beginner";
                } else if (dialogBinding.highSkillRadioBtn.isChecked()) {
                    skillLevel = "advanced";
                }
                StringBuilder ingredientsSb = new StringBuilder(ingredients);
                ingredientsSb.deleteCharAt(ingredientsSb.length() - 2); // Doing this to delete the last comma from ingredients
                skillPickerDialog.dismiss();
                ingredients = ingredientsSb.toString();
                loadingAdProgressDialog = new ProgressDialog(CreateRecipeActivity.this);
                loadingAdProgressDialog.setCancelable(false);
                loadingAdProgressDialog.setMessage("Please Wait...");
                loadingAdProgressDialog.show();
                AdUtils.showRewardedAd(CreateRecipeActivity.this);
            }
        });
    }

    public void getRecipe() {
        if (loadingAdProgressDialog != null && loadingAdProgressDialog.isShowing()) {
            loadingAdProgressDialog.dismiss();
        }
        showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constants.API_ENDPOINT, getBodyForThisSession(ingredients, skillLevel), response -> {
            try {
                long timestamp = response.getLong("created");
                JSONArray choices = response.getJSONArray("choices");
                JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                String content = message.getString("content");
                String replacedResponse = content.replace("\n\n", "\n").replace("\n", "");
                JSONObject object = new JSONObject(replacedResponse);
                Object recipe = object.get("recipe");
                String recipeText = "";
                if (recipe instanceof JSONArray) {
                    JSONArray recipeArray = (JSONArray) recipe;
                    for (int i = 0; i < recipeArray.length(); i++) {
                        recipeText = recipeText + "- " + recipeArray.get(i) + "\n\n";
                    }
                } else if (recipe instanceof String) {
                    recipeText = (String) recipe;
                }
                String name = object.getString("name");
                Recipe generatedRecipe = new Recipe(name, recipeText, timestamp, ingredients);
                startActivity(new Intent(CreateRecipeActivity.this, RecipeDetailActivity.class).putExtra(Constants.RECIPE, generatedRecipe).putExtra(Constants.IS_NEW_RECIPE, true));
                finish();
            } catch (JSONException e) {
                new AlertDialog.Builder(CreateRecipeActivity.this)
                        .setCancelable(false)
                        .setMessage("Something went wrong, please try again.")
                        .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                e.printStackTrace();
            } finally {
                closeProgressDialog();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + getResources().getString(R.string.open_ai_api_key));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    private JSONObject getBodyForThisSession(String ingredients, String cookLevel) {

        String query = "Please generate a recipe using these ingredients (" + ingredients + ") for a (" + cookLevel + ") cook. Give the response in JSON form. Put the recipe in 'recipe' key, ingredients in 'ingredients' key and name in 'name' key";
        JSONObject jsonObject = new JSONObject();
        JSONObject message = new JSONObject();
        JSONArray messages = new JSONArray();
        try {
            message.put("role", Constants.ROLE);
            message.put("content", query);
            messages.put(message);


            jsonObject.put("model", Constants.MODEL);
            jsonObject.put("messages", messages);
            jsonObject.put("temperature", Constants.TEMPERATURE);
            jsonObject.put("max_tokens", Constants.MAX_TOKENS);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showProgressDialog() {
        progressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.circular_progress_bar)
                .setCancelable(false)
                .create();
        progressDialog.show();
    }

    public void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void swapToolbar() {
        setSupportActionBar(binding.appBar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}